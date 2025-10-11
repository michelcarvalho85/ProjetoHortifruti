import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
public class TestaLeitura {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/hortisystem?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        String user = "root";
        String password = "root"; // altere se necessário

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nome, preco, quantidade, categoria FROM produto")) {

            boolean tem = false;
            while (rs.next()) {
                tem = true;
                System.out.println(
                    rs.getInt("id") + " - " +
                    rs.getString("nome") + " - " +
                    rs.getDouble("preco") + " - " +
                    rs.getInt("quantidade") + " - " +
                    rs.getString("categoria")
                );
            }
            if (!tem) System.out.println("❌ Nenhum produto encontrado!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
