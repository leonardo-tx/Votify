package br.com.votify.console.menus.auth;

import br.com.votify.console.callers.VotifyApiCaller;
import br.com.votify.console.menus.Menu;
import br.com.votify.console.utils.ConsoleUtils;
import br.com.votify.dto.ApiResponse;

import java.util.Scanner;

public class RefreshTokensMenu extends Menu {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        printBanner();

        ApiResponse<?> response = VotifyApiCaller.AUTH.refreshTokens();
        if (response.isSuccess()) {
            System.out.println("Tokens regenerados com sucesso");
        } else {
            System.out.println("Erro: " + response.getErrorMessage());
        }
        ConsoleUtils.pressEnterToContinue(scanner);
    }

    @Override
    public String getOptionName() {
        return "Regenerar tokens";
    }
}
