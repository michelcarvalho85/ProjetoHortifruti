package controller;

import model.Categoria;
import model.Produto;
import service.ProdutoService;

import java.util.List;
import java.util.Scanner;

public class ProdutoController {
    private ProdutoService service = new ProdutoService();
    private Scanner scanner = new Scanner(System.in);

    public void menu() {
        int opcao;
        do {
            System.out.println("\n=== MENU PRODUTO ===");
            System.out.println("1 - Adicionar Produto");
            System.out.println("2 - Listar Produtos");
            System.out.println("3 - Atualizar Produto");
            System.out.println("4 - Remover Produto");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1 -> adicionar();
                case 2 -> listar();
                case 3 -> atualizar();
                case 4 -> remover();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private void adicionar() {
        System.out.print("ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Preço: ");
        double preco = scanner.nextDouble();
        System.out.print("Quantidade: ");
        int qtd = scanner.nextInt();

        System.out.println("Categoria (FRUTAS, VERDURAS, LEGUMES, BEBIDAS, OUTROS): ");
        String cat = scanner.next().toUpperCase();

        Produto p = new Produto(id, nome, preco, qtd, Categoria.valueOf(cat));
        service.adicionarProduto(p);
        System.out.println("✅ Produto adicionado com sucesso!");
    }

    private void listar() {
        List<Produto> lista = service.listarProdutos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
        } else {
            lista.forEach(System.out::println);
        }
    }

    private void atualizar() {
        System.out.print("ID do produto a atualizar: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Novo nome: ");
        String nome = scanner.nextLine();
        System.out.print("Novo preço: ");
        double preco = scanner.nextDouble();
        System.out.print("Nova quantidade: ");
        int qtd = scanner.nextInt();
        System.out.println("Nova categoria: ");
        String cat = scanner.next().toUpperCase();

        Produto novo = new Produto(id, nome, preco, qtd, Categoria.valueOf(cat));
        if (service.atualizarProduto(id, novo)) {
            System.out.println("✅ Produto atualizado!");
        } else {
            System.out.println("❌ Produto não encontrado.");
        }
    }

    private void remover() {
        System.out.print("ID do produto a remover: ");
        int id = scanner.nextInt();
        if (service.removerProduto(id)) {
            System.out.println("✅ Produto removido!");
        } else {
            System.out.println("❌ Produto não encontrado.");
        }
    }
}
