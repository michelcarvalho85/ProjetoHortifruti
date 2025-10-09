package repository;

import model.Categoria;
import model.Produto;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoFileRepository {
    private final String arquivo = "produtos.txt";

    // 🔹 Salvar lista completa no arquivo
    public void salvarTudo(List<Produto> produtos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
            for (Produto p : produtos) {
                writer.write(p.getId() + ";" +
                             p.getNome() + ";" +
                             p.getPreco() + ";" +
                             p.getQuantidade() + ";" +
                             p.getCategoria());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    // 🔹 Ler produtos do arquivo
    public List<Produto> carregar() {
        List<Produto> produtos = new ArrayList<>();
        File file = new File(arquivo);
        if (!file.exists()) return produtos;

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(";");
                if (dados.length == 5) {
                    int id = Integer.parseInt(dados[0]);
                    String nome = dados[1];
                    double preco = Double.parseDouble(dados[2]);
                    int qtd = Integer.parseInt(dados[3]);
                    Categoria categoria = Categoria.valueOf(dados[4]);
                    produtos.add(new Produto(id, nome, preco, qtd, categoria));
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo: " + e.getMessage());
        }

        return produtos;
    }
}
