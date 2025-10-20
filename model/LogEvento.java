package model;

import java.time.LocalDateTime;

public class LogEvento {

    public enum Acao {
        LOGIN,
        ADICIONAR_PRODUTO,
        ATUALIZAR_PRODUTO,
        REMOVER_PRODUTO,
        OUTRA
    }

    private Long id;
    private LocalDateTime ts;
    private String usuario;
    private Acao acao;
    private String detalhes;

    public LogEvento() {}

    public LogEvento(String usuario, Acao acao, String detalhes) {
        this.usuario = usuario;
        this.acao = acao;
        this.detalhes = detalhes;
        this.ts = LocalDateTime.now();
    }

    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getTs() { return ts; }
    public void setTs(LocalDateTime ts) { this.ts = ts; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public Acao getAcao() { return acao; }
    public void setAcao(Acao acao) { this.acao = acao; }

    public String getDetalhes() { return detalhes; }
    public void setDetalhes(String detalhes) { this.detalhes = detalhes; }
}
