import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestaConexao {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/hortisystem?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String user = "root";
        String password = "root"; // altere conforme sua senha

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                System.out.println("✅ Conectado com sucesso ao MySQL!");
            } else {
                System.out.println("❌ Falha ao conectar: conexão nula.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Erro na conexão: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
