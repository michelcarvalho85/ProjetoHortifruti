package service;

import model.Usuario;
import repository.UsuarioRepository;

public class UsuarioService {
    private UsuarioRepository repository = new UsuarioRepository();

    public Usuario login(String login, String senha) {
        return repository.autenticar(login, senha);
    }
}
