package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    // ✅ PostgreSQL
    private static final String URL = "jdbc:postgresql://localhost:5432/hortisystem";
    private static final String USER = "postgres"; // ajuste se for diferente
    private static final String PASSWORD = "root";  // ajuste se for diferente

    public static Connection conectar() throws SQLException {
        try {
            // 🔹 Carrega o driver do PostgreSQL (opcional nas versões modernas, mas mantém claro)
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Driver PostgreSQL não encontrado! Verifique se o JAR está no classpath (ex.: lib/postgresql-42.7.4.jar).");
            throw new SQLException(e);
        } catch (SQLException e) {
            System.out.println("❌ Erro ao conectar ao PostgreSQL: " + e.getMessage());
            throw e;
        }
    }
}
