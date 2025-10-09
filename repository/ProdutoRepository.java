package repository;

import model.Produto;
import java.util.ArrayList;
import java.util.List;

public class ProdutoRepository {
    private List<Produto> produtos = new ArrayList<>();

    public void salvar(Produto produto) {
        produtos.add(produto);
    }

    public List<Produto> listar() {
        return produtos;
    }

    public Produto buscarPorId(int id) {
        for (Produto p : produtos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public boolean atualizar(int id, Produto produtoAtualizado) {
        Produto existente = buscarPorId(id);
        if (existente != null) {
            existente.setNome(produtoAtualizado.getNome());
            existente.setPreco(produtoAtualizado.getPreco());
            existente.setQuantidade(produtoAtualizado.getQuantidade());
            existente.setCategoria(produtoAtualizado.getCategoria());
            return true;
        }
        return false;
    }

    public boolean deletar(int id) {
        Produto produto = buscarPorId(id);
        if (produto != null) {
            produtos.remove(produto);
            return true;
        }
        return false;
    }
}
