package server;

import static spark.Spark.*;
import com.google.gson.Gson;
import repository.UsuarioRepository;
import model.Usuario;

public class WebServer {
    public static void main(String[] args) {
        port(8080);
        Gson gson = new Gson();
        UsuarioRepository usuarioRepo = new UsuarioRepository();

        // Libera o frontend
        options("/*", (req, res) -> {
            String headers = req.headers("Access-Control-Request-Headers");
            if (headers != null) res.header("Access-Control-Allow-Headers", headers);
            String methods = req.headers("Access-Control-Request-Method");
            if (methods != null) res.header("Access-Control-Allow-Methods", methods);
            return "OK";
        });
        before((req, res) -> res.header("Access-Control-Allow-Origin", "*"));

        // Rota de teste
        get("/ping", (req, res) -> "Servidor ativo!");

        // Login com usuários em memória
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
    }
}
