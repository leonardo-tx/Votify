package br.com.votify.console.menus.polls;

import br.com.votify.console.callers.VotifyApiCaller;
import br.com.votify.console.menus.Menu;
import br.com.votify.console.utils.ConsoleUtils;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.polls.PollInsertDTO;
import br.com.votify.dto.users.UserDetailedViewDTO;
import br.com.votify.dto.polls.VoteOptionInsertDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class CreatePollMenu extends Menu {
    private final Scanner scanner = new Scanner(System.in);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    public void run() {
        printBanner();

        PollInsertDTO dto = new PollInsertDTO();

        System.out.print("Insira o título: ");
        dto.setTitle(scanner.nextLine());

        System.out.print("Insira a descrição: ");
        dto.setDescription(scanner.nextLine());

        System.out.print("Insira a data e hora de início (Opcional) [dd/MM/yyyy HH:mm:ss]: ");
        String startDate = scanner.nextLine();
        dto.setStartDate(Objects.equals(startDate, "") ? null : LocalDateTime.parse(startDate, formatter));

        System.out.print("Insira a data e hora de término [dd/MM/yyyy HH:mm:ss]: ");
        dto.setEndDate(LocalDateTime.parse(scanner.nextLine(), formatter));

        System.out.print("A enquete possui registro de usuários? [S ou N]: ");
        dto.setUserRegistration(ConsoleUtils.getBooleanFromInput(scanner));

        System.out.print("Limite de escolhas por usuário: [1 - 5]: ");
        dto.setChoiceLimitPerUser(Integer.parseInt(scanner.nextLine()));

        System.out.print("Quantas escolhas haverá na enquete [1 - 5]: ");
        dto.setVoteOptions(new ArrayList<>());
        int i = Integer.parseInt(scanner.nextLine());

        for (int j = 0; j < i; j++) {
            VoteOptionInsertDTO voteOptionInsertDTO = new VoteOptionInsertDTO();
            System.out.print("Insira o nome da escolha " + (j + 1) + ": ");
            voteOptionInsertDTO.setName(scanner.nextLine());
            dto.getVoteOptions().add(voteOptionInsertDTO);
        }

        ApiResponse<UserDetailedViewDTO> response = VotifyApiCaller.POLLS.create(dto);
        ConsoleUtils.clear();
        if (response.isSuccess()) {
            System.out.println("Enquete criada com sucesso");
        } else {
            System.out.println("Erro: " + response.getErrorMessage());
        }
        ConsoleUtils.pressEnterToContinue(scanner);
    }

    @Override
    public String getOptionName() {
        return "Criar enquete";
    }
}
