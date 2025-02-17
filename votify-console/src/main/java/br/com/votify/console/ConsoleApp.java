package br.com.votify.console;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ConsoleApp {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8081/users/1"; // Ajuste o endpoint conforme necessário

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            System.out.println("Resposta da API: " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("Erro ao chamar a API: " + e.getMessage());
        }
    }
}
