package br.com.votify.console.menus.context;

import br.com.votify.console.callers.VotifyApiCaller;
import br.com.votify.console.menus.Menu;
import br.com.votify.console.utils.ConsoleUtils;
import br.com.votify.dto.ApiResponse;

import java.util.Scanner;

public class DeleteUserContextMenu extends Menu {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        ConsoleUtils.clear();
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("                 Deletar conta                 ");
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.print("Tem certeza? [S ou N]: ");

        if (!ConsoleUtils.getBooleanFromInput(scanner)) return;

        ApiResponse<?> response = VotifyApiCaller.CONTEXT.delete();
        if (response.isSuccess()) {
            System.out.println("Conta deletada com sucesso");
        } else {
            System.out.println("Erro: " + response.getErrorMessage());
        }
        ConsoleUtils.pressEnterToContinue(scanner);
    }

    @Override
    public String getOptionName() {
        return "Deletar conta";
    }
}
