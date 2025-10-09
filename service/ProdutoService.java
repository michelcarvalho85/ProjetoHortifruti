package service;

import model.Produto;
import repository.ProdutoRepository;
import java.util.List;

public class ProdutoService {
    private ProdutoRepository repository = new ProdutoRepository();

    public void adicionarProduto(Produto produto) {
        repository.salvar(produto);
    }

    public List<Produto> listarProdutos() {
        return repository.listar();
    }

    public boolean atualizarProduto(int id, Produto novoProduto) {
        return repository.atualizar(id, novoProduto);
    }

    public boolean removerProduto(int id) {
        return repository.deletar(id);
    }
}
