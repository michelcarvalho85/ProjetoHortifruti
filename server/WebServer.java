package server;

import static spark.Spark.*;

import com.google.gson.Gson;
import model.Produto;
import model.Usuario;
import repository.ProdutoDBRepository;
import repository.UsuarioRepository;
import service.LogService;

import java.util.List;

public class WebServer {
    public static void main(String[] args) {
        port(8080);
        Gson gson = new Gson();
        UsuarioRepository usuarioRepo = new UsuarioRepository();
        ProdutoDBRepository produtoRepo = new ProdutoDBRepository();
        LogService log = new LogService();

        // =========================
        // 🔓 CORS
        // =========================
        options("/*", (req, res) -> {
            String headers = req.headers("Access-Control-Request-Headers");
            if (headers != null) res.header("Access-Control-Allow-Headers", headers);
            String methods = req.headers("Access-Control-Request-Method");
            if (methods != null) res.header("Access-Control-Allow-Methods", methods);
            return "OK";
        });
        before((req, res) -> res.header("Access-Control-Allow-Origin", "*"));

        // =========================
        // 🧠 LOGIN
        // =========================
        post("/login", (req, res) -> {
            res.type("application/json");
            Usuario login = gson.fromJson(req.body(), Usuario.class);

            Usuario usuario = usuarioRepo.autenticar(login.getLogin(), login.getSenha());
            if (usuario != null) {
                log.registrar(usuario.getNome(), "Realizou login via Web");
                return gson.toJson(usuario);
            } else {
                res.status(401);
                return gson.toJson(new RespostaErro("Login ou senha inválidos"));
            }
        });

        // =========================
        // 🍎 PRODUTOS
        // =========================
        get("/produtos", (req, res) -> {
            res.type("application/json");
            List<Produto> produtos = produtoRepo.listar();
            return gson.toJson(produtos);
        });

        post("/produtos", (req, res) -> {
            res.type("application/json");
            Produto p = gson.fromJson(req.body(), Produto.class);
            produtoRepo.salvar(p);
            log.registrar("Sistema", "Adicionou produto: " + p.getNome());
            res.status(201);
            return gson.toJson(p);
        });

        put("/produtos/:id", (req, res) -> {
            res.type("application/json");
            int id = Integer.parseInt(req.params("id"));
            Produto novo = gson.fromJson(req.body(), Produto.class);
            boolean atualizado = produtoRepo.atualizar(id, novo);
            if (atualizado) {
                log.registrar("Sistema", "Atualizou produto ID " + id);
                return gson.toJson(novo);
            } else {
                res.status(404);
                return gson.toJson(new RespostaErro("Produto não encontrado"));
            }
        });

        delete("/produtos/:id", (req, res) -> {
            res.type("application/json");
            int id = Integer.parseInt(req.params("id"));
            boolean removido = produtoRepo.remover(id);
            if (removido) {
                log.registrar("Sistema", "Removeu produto ID " + id);
                res.status(204);
                return "";
            } else {
                res.status(404);
                return gson.toJson(new RespostaErro("Produto não encontrado"));
            }
        });

        // =========================
        // 🔍 PING (teste rápido)
        // =========================
        get("/ping", (req, res) -> "Servidor ativo!");
    }

    // Classes auxiliares simples para mensagens JSON
    static class RespostaErro {
        String mensagem;
        RespostaErro(String m) { this.mensagem = m; }
    }
}
