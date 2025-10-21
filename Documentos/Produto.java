package br.com.hortisystem.produto;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * =========================================
 * ENTITY: Produto
 * =========================================
 * Mapeamento para os diagramas:
 * - Diagrama de Classes (Produto): representa os ATRIBUTOS: id, nome, quantidade, valor, unidadeMedida, categoria.
 *   E os MÉTODOS de regra/consistência sugeridos: ValidarDados(), Visualizar().
 * - BCE: Esta classe é a "Entity".
 *
 * NOTA SOBRE MÉTODOS Cadastrar/Editar/Excluir:
 * - No BCE, a responsabilidade de orquestrar persistência é do "Control" (ProdutoController) + Repositório.
 * - Por isso, aqui ficamos com estado + validação + representação (SRP).
 */
public class Produto {

    private Integer id;                // id: int (Integer para permitir "null" antes de salvar)
    private String nome;               // nome: string
    private int quantidade;            // quantidade: int (>= 0)
    private BigDecimal valor;          // valor: decimal (> 0)
    private UnidadeMedida unidadeMedida; // unidadeMedida: string -> aqui como enum
    private String categoria;          // categoria: string

    // Construtor mínimo para criação (sem id)
    public Produto(String nome, int quantidade, BigDecimal valor, UnidadeMedida unidadeMedida, String categoria) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.valor = valor;
        this.unidadeMedida = unidadeMedida;
        this.categoria = categoria;
    }

    // Construtor completo (útil para rehidratar do banco)
    public Produto(Integer id, String nome, int quantidade, BigDecimal valor, UnidadeMedida unidadeMedida, String categoria) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
        this.valor = valor;
        this.unidadeMedida = unidadeMedida;
        this.categoria = categoria;
    }

    /**
     * Diagrama de Classes / BCE: ValidarDados()
     * Verifica consistência de domínio antes de qualquer operação de persistência.
     *
     * Caso de Uso (Cadastrar Produto) – passo "Sistema valida os dados".
     */
    public boolean validarDados() {
        if (nome == null || nome.isBlank()) return false;
        if (quantidade < 0) return false;
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) return false;
        if (unidadeMedida == null) return false;
        if (categoria == null || categoria.isBlank()) return false;
        return true;
    }

    /**
     * Diagrama de Classes / BCE: Visualizar()
     * Retorna uma representação legível do produto.
     * Útil para confirmação (passo final do caso de uso).
     */
    public String visualizar() {
        return String.format(
            "Produto{id=%s, nome='%s', quantidade=%d, valor=%s, unidade=%s, categoria='%s'}",
            id, nome, quantidade, valor, unidadeMedida, categoria
        );
    }

    // Getters/Setters (encapsulamento)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; } // geralmente setado pelo repositório ao salvar

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public UnidadeMedida getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(UnidadeMedida unidadeMedida) { this.unidadeMedida = unidadeMedida; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    // Igualdade por id (se houver), caso contrário por campos chave (nome+categoria)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Produto)) return false;
        Produto produto = (Produto) o;
        if (id != null && produto.id != null) return Objects.equals(id, produto.id);
        return Objects.equals(nome, produto.nome) && Objects.equals(categoria, produto.categoria);
    }

    @Override
    public int hashCode() {
        if (id != null) return Objects.hash(id);
        return Objects.hash(nome, categoria);
    }
}
