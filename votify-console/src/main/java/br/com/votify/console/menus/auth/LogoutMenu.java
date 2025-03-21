package br.com.votify.console.menus.auth;

import br.com.votify.console.callers.VotifyApiCaller;
import br.com.votify.console.menus.Menu;
import br.com.votify.console.utils.ConsoleUtils;
import br.com.votify.dto.ApiResponse;

import java.util.Scanner;

public class LogoutMenu extends Menu {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        printBanner();
        System.out.print("Tem certeza? [S ou N]: ");

        if (!ConsoleUtils.getBooleanFromInput(scanner)) return;

        ApiResponse<?> response = VotifyApiCaller.AUTH.logout();
        if (response.isSuccess()) {
            System.out.println("Deslogado com sucesso");
        } else {
            System.out.println("Erro: " + response.getErrorMessage());
        }
        ConsoleUtils.pressEnterToContinue(scanner);
    }

    @Override
    public String getOptionName() {
        return "Logout";
    }
}
