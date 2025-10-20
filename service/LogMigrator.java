package service;

import model.LogEvento.Acao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;

public class LogMigrator {

    private final LogServiceDB logServiceDB;

    public LogMigrator(LogServiceDB logServiceDB) {
        this.logServiceDB = logServiceDB;
    }

    /**
     * Espera linhas no formato:
     * 2025-10-14T12:34:56.789 | Usuario X | Mensagem texto livre
     */
    public void importarDoArquivo(String caminho) {
        File f = new File(caminho);
        if (!f.exists()) {
            System.out.println("ℹ️ Arquivo de log não encontrado, ignorando migração: " + caminho);
            return;
        }
        int ok = 0, falhas = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                try {
                    String[] partes = linha.split("\\|");
                    if (partes.length < 3) {
                        falhas++;
                        continue;
                    }
                    String tsStr   = partes[0].trim();
                    String usuario = partes[1].trim();
                    String msg     = linha.substring(linha.indexOf('|', linha.indexOf('|') + 1) + 1).trim();

                    // mapeia ação pela mensagem
                    Acao acao = mapearAcao(msg);
                    // registra usando API compatível (salva ts atual; se quiser, estenda LogServiceDB para aceitar ts)
                    logServiceDB.registrar(usuario, acao, msg);
                    ok++;
                } catch (Exception e) {
                    falhas++;
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erro na migração do log: " + e.getMessage());
        }
        System.out.println("✅ Migração concluída. OK=" + ok + " | Falhas=" + falhas);
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
