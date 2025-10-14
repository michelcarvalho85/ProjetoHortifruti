import java.util.List;

import model.Categoria;
import model.Produto;
import repository.ProdutoDBRepository;

/**
 * Classe de teste para verificar se o ID é gerado automaticamente (AUTO_INCREMENT)
 * e se as operações CRUD estão funcionando corretamente.
 */
public class TestaProdutoAutoIncrement {
    public static void main(String[] args) {
        ProdutoDBRepository repo = new ProdutoDBRepository();

        System.out.println("=== TESTE AUTO_INCREMENT ===");

        // 1️⃣ Inserir um produto (sem ID)
        Produto novo = new Produto(0, "Banana Prata", 4.75, 20, Categoria.FRUTAS);
        repo.salvar(novo);
        System.out.println("Produto inserido com ID gerado: " + novo.getId());

        // 2️⃣ Listar todos os produtos
        List<Produto> produtos = repo.listar();
        System.out.println("\nLista de produtos após inserção:");
        for (Produto p : produtos) {
            System.out.println(p);
        }

        // 3️⃣ Atualizar o primeiro produto
        if (!produtos.isEmpty()) {
            Produto p1 = produtos.get(0);
            p1.setPreco(p1.getPreco() + 1.0);
            repo.atualizar(p1.getId(), p1);
            System.out.println("\nProduto atualizado com novo preço: " + p1.getPreco());
        }

        // 4️⃣ Remover o produto adicionado
        if (!produtos.isEmpty()) {
            int idRemover = produtos.get(0).getId();
            repo.remover(idRemover);
            System.out.println("\nProduto removido com ID: " + idRemover);
        }

        // 5️⃣ Listar novamente
        produtos = repo.listar();
        System.out.println("\nLista final de produtos:");
        if (produtos.isEmpty()) {
            System.out.println("✅ Nenhum produto restante — teste concluído com sucesso!");
        } else {
            produtos.forEach(System.out::println);
        }
    }
}
