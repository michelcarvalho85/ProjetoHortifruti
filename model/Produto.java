package model;

public class Produto {
    private int id;
    private String nome;
    private double preco;
    private int quantidade;
    private Categoria categoria;

    // Construtor vazio (útil para Gson / frameworks)
    public Produto() { }

    public Produto(int id, String nome, double preco, int quantidade, Categoria categoria) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
        this.categoria = categoria;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public double getPreco() { return preco; }
    public int getQuantidade() { return quantidade; }
    public Categoria getCategoria() { return categoria; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setPreco(double preco) { this.preco = preco; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    @Override
    public String toString() {
        return "Produto { " +
                "ID=" + id +
                ", Nome='" + nome + '\'' +
                ", Preço=" + preco +
                ", Quantidade=" + quantidade +
                ", Categoria=" + categoria +
                " }";
    }
}
