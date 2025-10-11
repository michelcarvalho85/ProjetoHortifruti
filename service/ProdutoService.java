package service;

import java.util.List;
import java.io.FileWriter;
import java.io.FileReader;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
        return db.remover(id);
    }

    // ============================================================
    // 📦 EXPORTAR PRODUTOS DO BANCO PARA ARQUIVO JSON
    // ============================================================
    public void exportarParaJSON(String caminhoArquivo) {
        try {
            List<Produto> produtos = db.listar(); // busca todos no banco
            Gson gson = new Gson();
            String json = gson.toJson(produtos);

            FileWriter writer = new FileWriter(caminhoArquivo);
            writer.write(json);
            writer.close();

            System.out.println("✅ Produtos exportados com sucesso para: " + caminhoArquivo);
        } catch (Exception e) {
            System.err.println("❌ Erro ao exportar produtos: " + e.getMessage());
        }
    }

    // ============================================================
    // 📥 IMPORTAR PRODUTOS DE UM ARQUIVO JSON PARA O BANCO
    // ============================================================
    public void importarDeJSON(String caminhoArquivo) {
        try {
            Gson gson = new Gson();
            FileReader reader = new FileReader(caminhoArquivo);

            Type tipoLista = new TypeToken<List<Produto>>() {}.getType();
            List<Produto> produtos = gson.fromJson(reader, tipoLista);
            reader.close();

            for (Produto p : produtos) {
                db.salvar(p);
            }

            System.out.println("✅ Produtos importados do arquivo para o banco de dados com sucesso!");
        } catch (Exception e) {
            System.err.println("❌ Erro ao importar produtos: " + e.getMessage());
        }
    }
}
