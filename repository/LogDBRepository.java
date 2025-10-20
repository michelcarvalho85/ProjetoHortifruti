package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import model.LogEvento;
import model.LogEvento.Acao;

public class LogDBRepository {

    public LogDBRepository() {
        garantirTabela();
    }

    private Connection conectar() throws SQLException {
        return ConnectionFactory.conectar();
    }

    /** Cria a tabela de logs no PostgreSQL (se não existir) */
    private void garantirTabela() {
        final String ddl = """
            CREATE TABLE IF NOT EXISTS log_evento (
              id       BIGSERIAL PRIMARY KEY,
              ts       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
              usuario  VARCHAR(80) NOT NULL,
              acao     VARCHAR(30) NOT NULL,
              detalhes VARCHAR(255)
            );
            """;

        // (Opcional) Restringe valores de 'acao' para os mesmos do enum da aplicação
        final String check = """
            DO $$
            BEGIN
              IF NOT EXISTS (
                SELECT 1
                FROM   information_schema.table_constraints tc
                WHERE  tc.table_name = 'log_evento'
                AND    tc.constraint_type = 'CHECK'
                AND    tc.constraint_name = 'log_evento_acao_check'
              ) THEN
                ALTER TABLE log_evento
                  ADD CONSTRAINT log_evento_acao_check
                  CHECK (acao IN ('LOGIN','ADICIONAR_PRODUTO','ATUALIZAR_PRODUTO','REMOVER_PRODUTO','OUTRA'));
              END IF;
            END$$;
            """;

        try (Connection c = conectar(); Statement st = c.createStatement()) {
            st.execute(ddl);
            st.execute(check);
        } catch (SQLException e) {
            System.err.println("❌ Erro ao criar/verificar tabela log_evento: " + e.getMessage());
        }
    }

    public void salvar(LogEvento le) {
        final String sql = "INSERT INTO log_evento (ts, usuario, acao, detalhes) VALUES (?, ?, ?, ?)";
        try (Connection c = conectar(); PreparedStatement ps = c.prepareStatement(sql)) {
            Timestamp ts = Timestamp.valueOf(le.getTs() != null ? le.getTs() : LocalDateTime.now());
            ps.setTimestamp(1, ts);
            ps.setString(2, le.getUsuario());
            ps.setString(3, le.getAcao() != null ? le.getAcao().name() : Acao.OUTRA.name());
            ps.setString(4, le.getDetalhes());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Erro ao salvar log: " + e.getMessage());
        }
    }
}
