import controller.ProdutoController;
import controller.UsuarioController;
import model.Usuario;

public class Main {
    public static void main(String[] args) {
        UsuarioController usuarioController = new UsuarioController();
        Usuario usuario = usuarioController.autenticar();

        if (usuario != null) {
            ProdutoController produtoController = new ProdutoController(usuario.getNome());
            produtoController.menu();
        } else {
            System.out.println("Encerrando o sistema...");
        }
    }
}
