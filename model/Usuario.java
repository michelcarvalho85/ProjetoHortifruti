package model;

public class Usuario {
    private String nome;
    private String login;
    private String senha;
    private Role role;

    public enum Role {
        ADMIN,
        FUNCIONARIO
    }

    public Usuario(String nome, String login, String senha, Role role) {
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.role = role;
    }

    public String getNome() { return nome; }
    public String getLogin() { return login; }
    public String getSenha() { return senha; }
    public Role getRole() { return role; }

    @Override
    public String toString() {
        return nome + " (" + role + ")";
    }
}
