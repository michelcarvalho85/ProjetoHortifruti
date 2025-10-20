package server;

import static spark.Spark.*;

import com.google.gson.Gson;

import model.Produto;
import model.Usuario;

import repository.ProdutoDBRepository;
import repository.UsuarioRepository;

// 🚨 NOVO: usa o serviço de logs no banco
import service.LogServiceDB;
import model.LogEvento.Acao;

import java.util.List;

public class WebServer {

    public static void main(String[] args) {
        System.out.println("🚀 Iniciando servidor HortiSystem na porta 8080...");
        port(8080);

        final Gson gson = new Gson();
        final UsuarioRepository usuarioRepo = new UsuarioRepository();
        final ProdutoDBRepository produtoRepo = new ProdutoDBRepository();
        final LogServiceDB log = new LogServiceDB(); // ✅ agora grava no MySQL (ENUM)

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
            res.header("Access-Control-Allow-Headers", "Content-Type, X-User-Role, X-User-Name");
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
                // grava enum LOGIN no banco
                log.registrar(usuario.getNome(), Acao.LOGIN, "Login via Web");
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

            // ✅ exige ADMIN
            if (!isAdmin(req)) {
                res.status(403);
                return gson.toJson(new RespostaErro("Acesso negado: requer perfil ADMIN."));
            }

            try {
                Produto p = gson.fromJson(req.body(), Produto.class);

                if (p.getNome() == null || p.getNome().isBlank()) {
                    res.status(400);
                    return gson.toJson(new RespostaErro("Nome do produto é obrigatório."));
                }

                produtoRepo.salvar(p);

                String usuarioHeader = headerUsuario(req);
                log.registrar(usuarioHeader, Acao.ADICIONAR_PRODUTO, "Adicionou produto: " + p.getNome());

                res.status(201);
                return gson.toJson(p);

            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new RespostaErro("Erro ao salvar produto: " + e.getMessage()));
            }
        });

        put("/produtos/:id", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            System.out.println("📡 Requisição recebida -> PUT /produtos/" + id);
            res.type("application/json; charset=UTF-8");

            // ✅ exige ADMIN
            if (!isAdmin(req)) {
                res.status(403);
                return gson.toJson(new RespostaErro("Acesso negado: requer perfil ADMIN."));
            }

            try {
                Produto novo = gson.fromJson(req.body(), Produto.class);

                if (novo.getNome() == null || novo.getNome().isBlank()) {
                    res.status(400);
                    return gson.toJson(new RespostaErro("Nome do produto é obrigatório."));
                }

                boolean atualizado = produtoRepo.atualizar(id, novo);
                if (atualizado) {
                    String usuarioHeader = headerUsuario(req);
                    log.registrar(usuarioHeader, Acao.ATUALIZAR_PRODUTO, "Atualizou produto ID " + id);
                    return gson.toJson(novo);
                } else {
                    res.status(404);
                    return gson.toJson(new RespostaErro("Produto não encontrado."));
                }

            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new RespostaErro("Erro ao atualizar produto: " + e.getMessage()));
            }
        });

        delete("/produtos/:id", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));
            System.out.println("📡 Requisição recebida -> DELETE /produtos/" + id);
            res.type("application/json; charset=UTF-8");

            // ✅ exige ADMIN
            if (!isAdmin(req)) {
                res.status(403);
                return gson.toJson(new RespostaErro("Acesso negado: requer perfil ADMIN."));
            }

            try {
                boolean removido = produtoRepo.remover(id);
                if (removido) {
                    String usuarioHeader = headerUsuario(req);
                    log.registrar(usuarioHeader, Acao.REMOVER_PRODUTO, "Removeu produto ID " + id);
                    res.status(204);
                    return "";
                } else {
                    res.status(404);
                    return gson.toJson(new RespostaErro("Produto não encontrado."));
                }
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return gson.toJson(new RespostaErro("Erro ao remover produto: " + e.getMessage()));
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

    // ===== Helpers =====

    // Lê o header X-User-Role e verifica se é ADMIN
    private static boolean isAdmin(spark.Request req) {
        String role = req.headers("X-User-Role");
        return role != null && role.equalsIgnoreCase("ADMIN");
    }

    // Tenta obter o nome do usuário pelo header X-User-Name; se não vier, usa "Sistema"
    private static String headerUsuario(spark.Request req) {
        String nome = req.headers("X-User-Name");
        if (nome == null || nome.isBlank()) nome = "Sistema";
        return nome;
    }

    // Classe auxiliar para mensagens JSON de erro
    static class RespostaErro {
        String mensagem;
        RespostaErro(String m) { this.mensagem = m; }
    }
}
