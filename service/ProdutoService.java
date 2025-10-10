package service;

import java.util.List;

import model.Produto;
import repository.ProdutoDBRepository;

public class ProdutoService {
    private final ProdutoDBRepository db = new ProdutoDBRepository();

    // CREATE
    public void adicionarProduto(Produto produto) {
        db.salvar(produto);
    }

    // READ
    public List<Produto> listarProdutos() {
        return db.listar();
    }

    // UPDATE
    public boolean atualizarProduto(int id, Produto novoProduto) {
        return db.atualizar(id, novoProduto);
    }

    // DELETE
    public boolean removerProduto(int id) {
        return db.remover(id); // ✅ método correto agora
    }
}
