package server;

import static spark.Spark.*;

import com.google.gson.Gson;

import model.Usuario;
import model.Produto;
import model.Categoria;

import repository.UsuarioRepository;
import repository.ProdutoDBRepository;

import java.util.List;

public class WebServer {
    private static final Gson gson = new Gson();
    private static final UsuarioRepository usuarioRepo = new UsuarioRepository();
    private static final ProdutoDBRepository produtoRepo = new ProdutoDBRepository();

    public static void main(String[] args) {
        port(8080);

        // CORS
        options("/*", (req, res) -> {
            String headers = req.headers("Access-Control-Request-Headers");
            if (headers != null) res.header("Access-Control-Allow-Headers", headers);
            String methods = req.headers("Access-Control-Request-Method");
            if (methods != null) res.header("Access-Control-Allow-Methods", methods);
            return "OK";
        });
        before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.type("application/json");
        });

        // Health
        get("/ping", (req, res) -> "Servidor ativo!");

        // ---------- LOGIN ----------
        post("/login", (req, res) -> {
            Usuario user = gson.fromJson(req.body(), Usuario.class);
            Usuario autenticado = usuarioRepo.autenticar(user.getLogin(), user.getSenha());
            if (autenticado != null) {
                res.status(200);
                return gson.toJson(autenticado);
            } else {
                res.status(401);
                return gson.toJson("Usuário ou senha incorretos!");
            }
        });

        // ---------- PRODUTOS ----------
        // Listar
        get("/produtos", (req, res) -> {
            List<Produto> lista = produtoRepo.listar();
            return gson.toJson(lista);
        });

        // Criar
        post("/produtos", (req, res) -> {
            Produto p = gson.fromJson(req.body(), Produto.class);

            // Normaliza categoria se vier como string/singular
            if (p.getCategoria() == null) {
                String raw = req.body();
                p.setCategoria(Categoria.fromStringFlex(extrairCategoria(raw)));
            }

            // Gera id se não vier
            if (p.getId() == 0) {
                p.setId(produtoRepo.proximoId());
            }

            produtoRepo.salvar(p);
            res.status(201);
            return gson.toJson(p);
        });

        // Atualizar
        put("/produtos/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            Produto body = gson.fromJson(req.body(), Produto.class);

            Categoria cat = body.getCategoria();
            if (cat == null) cat = Categoria.fromStringFlex(extrairCategoria(req.body()));

            Produto novo = new Produto(
                id,
                body.getNome(),
                body.getPreco(),
                body.getQuantidade(),
                cat
            );

            boolean ok = produtoRepo.atualizar(id, novo);
            if (ok) {
                res.status(200);
                return gson.toJson(novo);
            } else {
                res.status(404);
                return gson.toJson("Produto não encontrado.");
            }
        });

        // Remover
        delete("/produtos/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            boolean ok = produtoRepo.remover(id);
            if (ok) {
                res.status(204);
                return "";
            } else {
                res.status(404);
                return gson.toJson("Produto não encontrado.");
            }
        });
    } // <-- fecha main

    // Extrai o valor de "categoria" do JSON cru (caso venha como string simples)
    private static String extrairCategoria(String rawJson) {
        if (rawJson == null) return null;
        String json = rawJson.trim();

        // procura "categoria": "VALOR"
        int idx = json.toLowerCase().indexOf("\"categoria\"");
        if (idx < 0) return null;

        int colon = json.indexOf(':', idx);
        if (colon < 0) return null;

        int j = colon + 1;
        while (j < json.length() && Character.isWhitespace(json.charAt(j))) j++;

        if (j < json.length() && (json.charAt(j) == '"' || json.charAt(j) == '\'')) {
            char quote = json.charAt(j++);
            int k = json.indexOf(quote, j);
            if (k > j) return json.substring(j, k);
        }

        int k = j;
        while (k < json.length() && json.charAt(k) != ',' && json.charAt(k) != '}' && !Character.isWhitespace(json.charAt(k))) k++;
        if (k > j) return json.substring(j, k);

        return null;
    } // <-- fecha extrairCategoria
} // <-- fecha classe
