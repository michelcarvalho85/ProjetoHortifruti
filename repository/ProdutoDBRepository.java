package repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // pode ficar para compat., mas não é obrigatório
import java.util.ArrayList;
import java.util.List;

import model.Categoria;
import model.Produto;

public class ProdutoDBRepository {

    public ProdutoDBRepository() {
        garantirTabela();
    }

    // Cria a tabela no PostgreSQL se não existir
    private void garantirTabela() {
        final String ddl = """
            CREATE TABLE IF NOT EXISTS produto (
                id         SERIAL PRIMARY KEY,
                nome       VARCHAR(120)  NOT NULL,
                preco      NUMERIC(10,2) NOT NULL,
                quantidade INTEGER       NOT NULL,
                categoria  VARCHAR(20)   NOT NULL
            );
            """;
        try (Connection conn = ConnectionFactory.conectar();
             Statement st = conn.createStatement()) {
            st.execute(ddl);
        } catch (SQLException e) {
            System.err.println("❌ Erro ao criar/verificar tabela produto: " + e.getMessage());
        }
    }

    // 🔍 Buscar produto por ID
    public Produto buscarPorId(int id) {
        final String sql = """
            SELECT id, nome, preco, quantidade, categoria
            FROM produto
            WHERE id = ?
            """;
        try (Connection conn = ConnectionFactory.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Produto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getDouble("preco"),
                        rs.getInt("quantidade"),
                        Categoria.valueOf(rs.getString("categoria").toUpperCase())
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao buscar produto por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ✅ CREATE (PostgreSQL com RETURNING id)
    public void salvar(Produto produto) {
        final String sql = """
            INSERT INTO produto (nome, preco, quantidade, categoria)
            VALUES (?, ?, ?, ?)
            RETURNING id
            """;
        try (Connection conn = ConnectionFactory.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, produto.getNome());
            ps.setDouble(2, produto.getPreco());
            ps.setInt(3, produto.getQuantidade());
            ps.setString(4, produto.getCategoria().name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    produto.setId(rs.getInt(1));
                }
            }

            System.out.println("✅ Produto salvo com sucesso: " + produto.getNome());
        } catch (SQLException e) {
            System.err.println("❌ Erro ao salvar produto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ✅ READ
    public List<Produto> listar() {
        List<Produto> produtos = new ArrayList<>();
        final String sql = """
            SELECT id, nome, preco, quantidade, categoria
            FROM produto
            ORDER BY id
            """;
        try (Connection conn = ConnectionFactory.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("📦 Executando SELECT em produto ...");
            while (rs.next()) {
                Produto p = new Produto(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getDouble("preco"),
                    rs.getInt("quantidade"),
                    Categoria.valueOf(rs.getString("categoria").toUpperCase())
                );
                produtos.add(p);
            }
            System.out.println("✅ Produtos retornados: " + produtos.size());
        } catch (SQLException e) {
            System.err.println("❌ Erro ao listar produtos: " + e.getMessage());
            e.printStackTrace();
        }
        return produtos;
    }

    // ✅ UPDATE
    public boolean atualizar(int id, Produto novo) {
        if (novo == null) {
            System.err.println("⚠️ Produto nulo recebido para atualização (ID: " + id + ")");
            return false;
        }
        final String sql = """
            UPDATE produto
               SET nome = ?, preco = ?, quantidade = ?, categoria = ?
             WHERE id = ?
            """;
        try (Connection conn = ConnectionFactory.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, novo.getNome());
            ps.setDouble(2, novo.getPreco());
            ps.setInt(3, novo.getQuantidade());
            ps.setString(4, novo.getCategoria().name());
            ps.setInt(5, id);

            int linhasAfetadas = ps.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.println("🟢 Produto atualizado com sucesso (ID " + id + ")");
                return true;
            } else {
                System.out.println("⚠️ Nenhum produto encontrado para atualização (ID " + id + ")");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao atualizar produto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ✅ DELETE
    public boolean remover(int id) {
        final String sql = "DELETE FROM produto WHERE id = ?";
        try (Connection conn = ConnectionFactory.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                System.out.println("🗑️ Produto removido (ID " + id + ")");
                return true;
            } else {
                System.out.println("⚠️ Nenhum produto encontrado para remoção (ID " + id + ")");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao remover produto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
