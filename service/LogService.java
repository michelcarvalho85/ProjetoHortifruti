package service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class LogService {
    private final String caminho = "logs/log.txt";

    public void registrar(String usuario, String acao) {
        try (FileWriter writer = new FileWriter(caminho, true)) {
            String linha = LocalDateTime.now() + " | " + usuario + " | " + acao + "\n";
            writer.write(linha);
        } catch (IOException e) {
            System.out.println("Erro ao registrar log: " + e.getMessage());
        }
    }
}
