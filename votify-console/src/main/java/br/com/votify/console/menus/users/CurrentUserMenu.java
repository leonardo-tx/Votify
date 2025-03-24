package br.com.votify.console.menus.users;

import br.com.votify.console.callers.VotifyApiCaller;
import br.com.votify.console.menus.Menu;
import br.com.votify.console.utils.ConsoleUtils;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.UserDetailedViewDTO;

import java.util.Scanner;

public class CurrentUserMenu extends Menu {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        printBanner();

        ApiResponse<UserDetailedViewDTO> response = VotifyApiCaller.USERS.getUser();
        if (response.isSuccess()) {
            System.out.println("Suas informações: " + response.getData());
        } else {
            System.out.println("Erro: " + response.getErrorMessage());
        }
        ConsoleUtils.pressEnterToContinue(scanner);
    }

    @Override
    public String getOptionName() {
        return "Minhas informações";
    }
}
