import model.Categoria;
import model.Produto;
import repository.ProdutoDBRepository;

public class TestaProduto {
    public static void main(String[] args) {
        ProdutoDBRepository repo = new ProdutoDBRepository();

        // inserir
        Produto p = new Produto(0, "Banana Prata", 5.99, 10, Categoria.FRUTAS);
        repo.salvar(p);
        System.out.println("Novo ID: " + p.getId());

        // listar
        System.out.println(repo.listar());

        // atualizar
        p.setPreco(4.49);
        repo.atualizar(p.getId(), p);

        // buscar
        System.out.println(repo.buscarPorId(p.getId()));

        // remover
        repo.remover(p.getId());
    }
}
