package br.com.votify.console.menus.auth;

import br.com.votify.console.callers.VotifyApiCaller;
import br.com.votify.console.menus.Menu;
import br.com.votify.console.utils.ConsoleUtils;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.UserDetailedViewDTO;
import br.com.votify.dto.users.UserRegisterDTO;

import java.util.Scanner;

public class RegisterMenu extends Menu {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        printBanner();

        UserRegisterDTO dto = new UserRegisterDTO();

        System.out.print("Insira o e-mail: ");
        dto.setEmail(scanner.nextLine());

        System.out.print("Insira o nome: ");
        dto.setName(scanner.nextLine());

        System.out.print("Insira o nome de usuário: ");
        dto.setUserName(scanner.nextLine());

        System.out.print("Insira a senha: ");
        dto.setPassword(scanner.nextLine());

        ApiResponse<UserDetailedViewDTO> response = VotifyApiCaller.AUTH.register(dto);
        ConsoleUtils.clear();
        if (response.isSuccess()) {
            System.out.println("Usuário criado com sucesso");
        } else {
            System.out.println("Erro: " + response.getErrorMessage());
        }
        ConsoleUtils.pressEnterToContinue(scanner);
    }

    @Override
    public String getOptionName() {
        return "Registrar usuário";
    }
}
