import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestaConexao {
    public static void main(String[] args) {
        // ✅ PostgreSQL
        String url = "jdbc:postgresql://localhost:5432/hortisystem";
        String user = "postgres";   // ajuste se o seu usuário for diferente
        String password = "root";   // ajuste se sua senha for diferente

        try {
            // (Opcional nas versões modernas, mas ajuda a diagnosticar classpath)
            Class.forName("org.postgresql.Driver");

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                if (conn != null) {
                    System.out.println("✅ Conectado com sucesso ao PostgreSQL!");
                } else {
                    System.out.println("❌ Falha ao conectar: conexão nula.");
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Driver PostgreSQL não encontrado! Coloque o JAR no classpath (ex.: lib/postgresql-42.7.4.jar).");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Erro na conexão: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
