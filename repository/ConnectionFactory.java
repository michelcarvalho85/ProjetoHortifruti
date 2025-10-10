package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static final String URL = "jdbc:mysql://localhost:3306/hortisystem?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static Connection conectar() throws SQLException {
        try {
            // 🔹 Força o carregamento do driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Driver MySQL não encontrado! Verifique o classpath.");
            throw new SQLException(e);
        } catch (SQLException e) {
            System.out.println("❌ Erro ao conectar ao banco: " + e.getMessage());
            throw e;
        }
    }
}
