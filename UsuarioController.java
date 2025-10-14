package controller;

import java.util.Scanner;

import model.Usuario;
import repository.UsuarioRepository;
import service.LogService;

/**
 * Controlador de autenticação de usuários.
 * Retorna o objeto Usuario autenticado para o Main.
 */
public class UsuarioController {

    private final UsuarioRepository repo = new UsuarioRepository();
    private final LogService log = new LogService();
    private final Scanner scanner = new Scanner(System.in);

    public Usuario autenticar() {
        System.out.println("=== LOGIN HORTISYSTEM ===");

        System.out.print("Login: ");
        String login = scanner.nextLine();

        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        Usuario usuario = repo.autenticar(login, senha);

        if (usuario != null) {
            System.out.println("\n✅ Login bem-sucedido!");
            System.out.println("Bem-vindo, " + usuario.getNome() + " (" + usuario.getRole() + ")");
            log.registrar(usuario.getNome(), "Realizou login no sistema");
            return usuario;
        } else {
            System.out.println("\n❌ Login ou senha inválidos!");
            log.registrar("Sistema", "Tentativa de login falhou para login: " + login);
            return null;
        }
    }
}
