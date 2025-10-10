package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Categoria;
import model.Produto;

public class ProdutoDBRepository {

    private final String URL = "jdbc:mysql://localhost:3306/hortisystem?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private final String USER = "root";
    private final String PASSWORD = "root"; // altere se necessário

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // ✅ CREATE
    public void salvar(Produto produto) {
        String sql = "INSERT INTO produto (id, nome, preco, quantidade, categoria) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, produto.getId());
            stmt.setString(2, produto.getNome());
            stmt.setDouble(3, produto.getPreco());
            stmt.setInt(4, produto.getQuantidade());
            stmt.setString(5, produto.getCategoria().name());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("❌ Erro ao salvar produto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ✅ READ
    public List<Produto> listar() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produto ORDER BY id";
        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                produtos.add(new Produto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getDouble("preco"),
                        rs.getInt("quantidade"),
                        Categoria.valueOf(rs.getString("categoria"))
                ));
            }
        } catch (SQLException e) {
            System.out.println("❌ Erro ao listar produtos: " + e.getMessage());
            e.printStackTrace();
        }
        return produtos;
    }

    // ✅ UPDATE
    public boolean atualizar(int id, Produto novo) {
        String sql = "UPDATE produto SET nome=?, preco=?, quantidade=?, categoria=? WHERE id=?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novo.getNome());
            stmt.setDouble(2, novo.getPreco());
            stmt.setInt(3, novo.getQuantidade());
            stmt.setString(4, novo.getCategoria().name());
            stmt.setInt(5, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Erro ao atualizar produto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ✅ DELETE (renomeado de deletar → remover)
    public boolean remover(int id) {
        String sql = "DELETE FROM produto WHERE id=?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("❌ Erro ao remover produto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
