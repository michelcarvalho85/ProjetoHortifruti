package controller;

import model.Usuario;
import service.UsuarioService;

import java.util.Scanner;

public class UsuarioController {
    private UsuarioService service = new UsuarioService();
    private Scanner scanner = new Scanner(System.in);

    public Usuario autenticar() {
        System.out.println("=== LOGIN ===");
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        Usuario usuario = service.login(login, senha);
        if (usuario != null) {
            System.out.println("✅ Bem-vindo, " + usuario.getNome() + "!");
        } else {
            System.out.println("❌ Usuário ou senha inválidos.");
        }
        return usuario;
    }
}
