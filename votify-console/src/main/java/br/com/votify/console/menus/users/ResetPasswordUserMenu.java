package br.com.votify.console.menus.users;

import br.com.votify.console.callers.VotifyApiCaller;
import br.com.votify.console.menus.Menu;
import br.com.votify.console.utils.ConsoleUtils;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.users.PasswordResetConfirmDTO;
import br.com.votify.dto.users.PasswordResetRequestDTO;
import br.com.votify.dto.users.PasswordResetResponseDTO;

import java.util.Scanner;

public class ResetPasswordUserMenu extends Menu {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        ConsoleUtils.clear();
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("                Esqueci a senha                ");
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

        PasswordResetRequestDTO dto1 = new PasswordResetRequestDTO();

        System.out.print("Insira o e-mail: ");
        dto1.setEmail(scanner.nextLine());

        ApiResponse<PasswordResetResponseDTO> response1 = VotifyApiCaller.PASSWORD.forgot(dto1);
        if (!response1.isSuccess()) {
            System.out.println("Erro: " + response1.getErrorMessage());
            ConsoleUtils.pressEnterToContinue(scanner);

            return;
        }
        System.out.println("Como não há serviço de e-mail, aqui está o código: " + response1.getData());
        PasswordResetConfirmDTO dto2 = new PasswordResetConfirmDTO();

        System.out.print("Insira o código: ");
        dto2.setCode(scanner.nextLine());

        System.out.print("Insira a nova senha: ");
        dto2.setNewPassword(scanner.nextLine());

        ApiResponse<?> response2 = VotifyApiCaller.PASSWORD.reset(dto2);
        ConsoleUtils.clear();
        if (response2.isSuccess()) {
            System.out.println("Reset de senha feito com sucesso");
        } else {
            System.out.println("Erro: " + response2.getErrorMessage());
        }
        ConsoleUtils.pressEnterToContinue(scanner);
    }

    @Override
    public String getOptionName() {
        return "Esqueci a senha";
    }
}
