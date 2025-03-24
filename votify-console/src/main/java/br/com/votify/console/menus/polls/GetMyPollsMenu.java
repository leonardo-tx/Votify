package br.com.votify.console.menus.polls;

import br.com.votify.console.callers.VotifyApiCaller;
import br.com.votify.console.menus.Menu;
import br.com.votify.console.utils.ConsoleUtils;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.PageResponse;
import br.com.votify.dto.polls.PollListViewDTO;

import java.util.Scanner;

public class GetMyPollsMenu extends Menu {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void run() {
        printBanner();

        ApiResponse<PageResponse<PollListViewDTO>> response = VotifyApiCaller.POLLS.getMyPolls();
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
        return "Resgatar minhas enquetes";
    }
}
