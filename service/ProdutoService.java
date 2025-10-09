package service;

import java.util.List;
import model.Produto;
import repository.ProdutoFileRepository;
import repository.ProdutoRepository;

public class ProdutoService {
    private ProdutoRepository memoria = new ProdutoRepository();
    private ProdutoFileRepository arquivo = new ProdutoFileRepository();

    public ProdutoService() {
        // Carrega produtos do arquivo ao iniciar
        List<Produto> salvos = arquivo.carregar();
        salvos.forEach(memoria::salvar);
    }

    public void adicionarProduto(Produto produto) {
        memoria.salvar(produto);
        arquivo.salvarTudo(memoria.listar());
    }

    public List<Produto> listarProdutos() {
        return memoria.listar();
    }

    public boolean atualizarProduto(int id, Produto novoProduto) {
        boolean ok = memoria.atualizar(id, novoProduto);
        if (ok) arquivo.salvarTudo(memoria.listar());
        return ok;
    }

    public boolean removerProduto(int id) {
        boolean ok = memoria.deletar(id);
        if (ok) arquivo.salvarTudo(memoria.listar());
        return ok;
    }
}
