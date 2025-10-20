package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import model.Usuario;

/**
 * Repositório de usuários no PostgreSQL (via ConnectionFactory).
 * - Cria a tabela se não existir
 * - Garante usuários padrão: admin/123 (ADMIN) e func/123 (FUNCIONARIO)
 * - Mantém a mesma interface: autenticar(login, senha)
 */
public class UsuarioRepository {

    public UsuarioRepository() {
        inicializarTabelaEUsuariosPadrao();
    }

    /** Cria tabela e insere usuários padrão se necessário */
    private void inicializarTabelaEUsuariosPadrao() {
        final String ddl = """
            CREATE TABLE IF NOT EXISTS usuario (
                id      SERIAL PRIMARY KEY,
                nome    VARCHAR(100) NOT NULL,
                login   VARCHAR(30)  NOT NULL UNIQUE,
                senha   VARCHAR(255) NOT NULL,
                funcao  VARCHAR(20)  NOT NULL
            );
            """;

        // Insere admin se não existir
        final String insAdmin = """
            INSERT INTO usuario (nome, login, senha, funcao)
            SELECT 'Administrador', 'admin', '123', 'ADMIN'
            WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE login = 'admin');
            """;

        // Insere func se não existir
        final String insFunc = """
            INSERT INTO usuario (nome, login, senha, funcao)
            SELECT 'Funcionário', 'func', '123', 'FUNCIONARIO'
            WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE login = 'func');
            """;

        try (Connection c = ConnectionFactory.conectar();
             Statement st = c.createStatement()) {
            st.execute(ddl);
            st.executeUpdate(insAdmin);
            st.executeUpdate(insFunc);
        } catch (SQLException e) {
            System.err.println("❌ Erro ao inicializar tabela/usuarios padrão: " + e.getMessage());
        }
    }

    /**
     * Autentica o usuário no banco:
     * retorna um Usuario se (login, senha) baterem, senão null.
     */
    public Usuario autenticar(String login, String senha) {
        final String sql = """
            SELECT nome, login, senha, funcao
            FROM usuario
            WHERE login = ? AND senha = ?
            LIMIT 1
            """;

        try (Connection c = ConnectionFactory.conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, login);
            ps.setString(2, senha);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("nome");
                    String log  = rs.getString("login");
                    String sen  = rs.getString("senha");
                    String fun  = rs.getString("funcao");

                    Usuario.Role role;
                    try {
                        role = Usuario.Role.valueOf(fun.toUpperCase());
                    } catch (Exception ex) {
                        role = Usuario.Role.FUNCIONARIO; // fallback
                    }

                    return new Usuario(nome, log, sen, role);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao autenticar usuário: " + e.getMessage());
        }
        return null;
    }
}
