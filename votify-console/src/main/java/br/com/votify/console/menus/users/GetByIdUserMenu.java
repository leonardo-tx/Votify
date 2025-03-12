package br.com.votify.console.menus.users;

import br.com.votify.console.callers.VotifyApiCaller;
import br.com.votify.console.menus.Menu;
import br.com.votify.console.utils.ConsoleUtils;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.UserQueryDTO;

import java.util.Scanner;

public class GetByIdUserMenu extends Menu {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        ConsoleUtils.clear();
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("           Procurar usuário pelo ID            ");
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

        System.out.print("Insira o ID: ");
        String id = scanner.nextLine();

        ApiResponse<UserQueryDTO> response = VotifyApiCaller.USERS.getUserById(id);
        ConsoleUtils.clear();
        if (response.isSuccess()) {
            System.out.println("Usuário encontrado: " + response.getData());
        } else {
            System.out.println("Erro: " + response.getErrorMessage());
        }
        ConsoleUtils.pressEnterToContinue(scanner);
    }

    @Override
    public String getOptionName() {
        return "Procurar usuário pelo ID";
    }
}
