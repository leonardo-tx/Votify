package br.com.votify.console.menus.polls;

import br.com.votify.console.callers.VotifyApiCaller;
import br.com.votify.console.menus.Menu;
import br.com.votify.console.utils.ConsoleUtils;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.PageResponse;
import br.com.votify.dto.polls.PollListViewDTO;

import java.util.Scanner;

public class GetUserPollsMenu extends Menu {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        ConsoleUtils.clear();
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        System.out.println("         Resgatar enquetes do usuário          ");
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");

        System.out.print("Insira o ID: ");
        String id = scanner.nextLine();

        ApiResponse<PageResponse<PollListViewDTO>> response = VotifyApiCaller.POLLS.getUserPolls(id);
        ConsoleUtils.clear();
        if (response.isSuccess()) {
            for (int i = 0; i < response.getData().getContent().size(); i++) {
                System.out.println(response.getData().getContent().get(i));
            }
        } else {
            System.out.println("Erro: " + response.getErrorMessage());
        }
        ConsoleUtils.pressEnterToContinue(scanner);
    }

    @Override
    public String getOptionName() {
        return "Resgatar enquetes do usuário";
    }
}
