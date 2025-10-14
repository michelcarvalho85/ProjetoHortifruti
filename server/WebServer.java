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
        System.out.println("🚀 Iniciando servidor HortiSystem na porta 8080...");
        port(8080);
        Gson gson = new Gson();
        UsuarioRepository usuarioRepo = new UsuarioRepository();
        ProdutoDBRepository produtoRepo = new ProdutoDBRepository();
        LogService log = new LogService();

        // =========================
        // 🌐 CORS — Libera acesso entre localhost e 127.0.0.1
        // =========================
        options("/*", (req, res) -> {
            String reqHeaders = req.headers("Access-Control-Request-Headers");
            if (reqHeaders != null) res.header("Access-Control-Allow-Headers", reqHeaders);
            String reqMethod = req.headers("Access-Control-Request-Method");
            if (reqMethod != null) res.header("Access-Control-Allow-Methods", reqMethod);
            return "OK";
        });

        before((req, res) -> {
            String origin = req.headers("Origin");
            if ("http://127.0.0.1:5500".equals(origin) || "http://localhost:5500".equals(origin)) {
                res.header("Access-Control-Allow-Origin", origin);
            } else {
                res.header("Access-Control-Allow-Origin", "*");
            }
            res.header("Access-Control-Allow-Credentials", "true");
            res.header("Access-Control-Allow-Headers", "Content-Type, X-User-Role");
            res.type("application/json; charset=UTF-8");
        });

        System.out.println("✅ Servidor iniciado com sucesso!");

        // =========================
        // 🧠 LOGIN
        // =========================
        post("/login", (req, res) -> {
            System.out.println("📡 Requisição recebida -> POST /login");
            res.type("application/json; charset=UTF-8");
            Usuario login = gson.fromJson(req.body(), Usuario.class);

            Usuario usuario = usuarioRepo.autenticar(login.getLogin(), login.getSenha());
            if (usuario != null) {
                System.out.println("✅ Login bem-sucedido: " + usuario.getLogin() + " (" + usuario.getRole() + ")");
                log.registrar(usuario.getNome(), "Realizou login via Web");
                return gson.toJson(usuario);
            } else {
                System.out.println("❌ Falha de login para: " + login.getLogin());
                res.status(401);
                return gson.toJson(new RespostaErro("Login ou senha inválidos"));
            }
        });

        // =========================
        // 🍎 PRODUTOS
        // =========================
        get("/produtos", (req, res) -> {
            System.out.println("📡 Requisição recebida -> GET /produtos");
            res.type("application/json; charset=UTF-8");
            List<Produto> produtos = produtoRepo.listar();
            System.out.println("✅ Produtos retornados: " + produtos.size());
            return gson.toJson(produtos);
        });

        post("/produtos", (req, res) -> {
            System.out.println("📡 Requisição recebida -> POST /produtos");
            res.type("application/json; charset=UTF-8");
            Produto p = gson.fromJson(req.body(), Produto.class);
            produtoRepo.salvar(p);
            log.registrar("Sistema", "Adicionou produto: " + p.getNome());
            res.status(201);
            return gson.toJson(p);
        });

        put("/produtos/:id", (req, res) -> {
            System.out.println("📡 Requisição recebida -> PUT /produtos/" + req.params("id"));
            res.type("application/json; charset=UTF-8");
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
            System.out.println("📡 Requisição recebida -> DELETE /produtos/" + req.params("id"));
            res.type("application/json; charset=UTF-8");
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
        get("/ping", (req, res) -> {
            System.out.println("📡 GET /ping → Servidor ativo!");
            res.type("text/plain");
            return "Servidor ativo!";
        });
    }

    // Classe auxiliar simples para mensagens JSON
    static class RespostaErro {
        String mensagem;
        RespostaErro(String m) { this.mensagem = m; }
    }
}
