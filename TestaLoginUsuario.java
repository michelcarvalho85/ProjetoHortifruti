import model.Usuario;
import repository.UsuarioRepository;

public class TestaLoginUsuario {
    public static void main(String[] args) {
        UsuarioRepository repo = new UsuarioRepository();

        System.out.println("=== TESTE DE LOGIN ===");

        Usuario u1 = repo.autenticar("admin", "1234");
        if (u1 != null) {
            System.out.println("✅ Login bem-sucedido! Usuário: " + u1.getNome() + " (" + u1.getRole() + ")");
        } else {
            System.out.println("❌ Falha no login!");
        }

        Usuario u2 = repo.autenticar("func", "9999");
        if (u2 == null) {
            System.out.println("✅ Falha correta: senha incorreta detectada.");
        }
    }
}
