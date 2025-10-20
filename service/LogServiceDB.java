package service;

import model.LogEvento;
import model.LogEvento.Acao;
import repository.LogDBRepository;

public class LogServiceDB {

    private final LogDBRepository repo = new LogDBRepository();

    /** API nova — explícita com enum e detalhes */
    public void registrar(String usuario, Acao acao, String detalhes) {
        if (usuario == null || usuario.isBlank()) usuario = "Sistema";
        if (acao == null) acao = Acao.OUTRA;
        LogEvento le = new LogEvento(usuario, acao, detalhes);
        repo.salvar(le);
    }

    /** API antiga — mantém compatibilidade, tenta mapear a string para uma Acao */
    public void registrar(String usuario, String mensagemLivre) {
        Acao acao = mapearAcao(mensagemLivre);
        registrar(usuario, acao, mensagemLivre);
    }

    private Acao mapearAcao(String msg) {
        if (msg == null) return Acao.OUTRA;
        String m = msg.toUpperCase();
        if (m.contains("LOGIN")) return Acao.LOGIN;
        if (m.contains("ADICIONOU PRODUTO") || m.contains("INSERIU PRODUTO")) return Acao.ADICIONAR_PRODUTO;
        if (m.contains("ATUALIZOU PRODUTO") || m.contains("ALTEROU PRODUTO")) return Acao.ATUALIZAR_PRODUTO;
        if (m.contains("REMOVEU PRODUTO") || m.contains("DELETOU PRODUTO") || m.contains("EXCLUIU PRODUTO")) return Acao.REMOVER_PRODUTO;
        return Acao.OUTRA;
    }
}
