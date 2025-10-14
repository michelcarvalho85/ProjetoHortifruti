package repository;

import java.util.ArrayList;
import java.util.List;

import model.Usuario;

/**
 * Repositório simples de usuários em memória
 * (para teste do login web)
 */
public class UsuarioRepository {
    private List<Usuario> usuarios = new ArrayList<>();

    public UsuarioRepository() {
        // Usuários pré-cadastrados
        usuarios.add(new Usuario("Administrador", "admin", "123", Usuario.Role.ADMIN));
        usuarios.add(new Usuario("Funcionário", "func", "123", Usuario.Role.FUNCIONARIO));
    }

    public Usuario autenticar(String login, String senha) {
        for (Usuario u : usuarios) {
            if (u.getLogin().equals(login) && u.getSenha().equals(senha)) {
                return u;
            }
        }
        return null;
    }
}
