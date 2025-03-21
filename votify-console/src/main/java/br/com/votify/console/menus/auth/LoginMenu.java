package br.com.votify.console.menus.auth;

import br.com.votify.console.callers.VotifyApiCaller;
import br.com.votify.console.menus.Menu;
import br.com.votify.console.utils.ConsoleUtils;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.UserLoginDTO;

import java.util.Scanner;

public class LoginMenu extends Menu {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        printBanner();

        UserLoginDTO dto = new UserLoginDTO();

        System.out.print("Insira o e-mail: ");
        dto.setEmail(scanner.nextLine());

        System.out.print("Insira a senha: ");
        dto.setPassword(scanner.nextLine());

        ApiResponse<?> response = VotifyApiCaller.AUTH.login(dto);
        ConsoleUtils.clear();
        if (response.isSuccess()) {
            System.out.println("Login feito com sucesso");
        } else {
            System.out.println("Erro: " + response.getErrorMessage());
        }
        ConsoleUtils.pressEnterToContinue(scanner);
    }

    @Override
    public String getOptionName() {
        return "Fazer login";
    }
}
