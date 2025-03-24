/*package br.com.votify.api.controller.poll;

import br.com.votify.core.domain.entities.polls.Poll;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.domain.entities.polls.Vote;
import br.com.votify.core.domain.entities.polls.VoteOption;
import br.com.votify.core.repository.VoteRepository;
import br.com.votify.core.service.ContextService;
import br.com.votify.core.service.PollService;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.polls.PollQueryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.mockito.Mockito.when;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PollControllerTest {

    @InjectMocks
    private PollController pollController;

    @Mock
    private PollService pollService;

    @Mock
    private ContextService contextService;

    @Mock
    private VoteRepository voteRepository;

    private Poll poll;

    @BeforeEach
    void setup() {
        User responsible = new CommonUser();
        responsible.setId(99L);

        VoteOption option = new VoteOption();
        option.setId(1L);
        option.setName("Opção A");

        poll = new Poll();
        poll.setId(10L);
        poll.setTitle("Enquete Teste");
        poll.setDescription("Descrição");
        poll.setStartDate(LocalDateTime.now());
        poll.setEndDate(LocalDateTime.now().plusDays(1));
        poll.setChoiceLimitPerUser(1);
        poll.setResponsible(responsible);
        poll.setVoteOptions(List.of(option));

        option.setPoll(poll); // linka a opção à enquete
    }

    @Test
    void deveRetornarEnqueteParaUsuarioNaoLogado() {
        when(contextService.getUserOptional()).thenReturn(Optional.empty());
        when(pollService.findSpecificPoll(10L)).thenReturn(poll);

        VoteOption option = new VoteOption();
        option.setId(1L);
        option.setName("Opção A");

        Poll poll = new Poll();
        poll.setId(10L);
        poll.setTitle("Enquete Teste");
        poll.setDescription("Descrição");
        poll.setStartDate(LocalDateTime.now());
        poll.setEndDate(LocalDateTime.now().plusDays(1));
        poll.setChoiceLimitPerUser(1);
        poll.setVoteOptions(new ArrayList<>(List.of(option)));

        User responsible = new CommonUser();
        responsible.setId(99L);
        poll.setResponsible(responsible);

        option.setPoll(poll);

        when(pollService.findSpecificPoll(10L)).thenReturn(poll);

        ResponseEntity<ApiResponse<PollQueryDto>> response = pollController.getPollById(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PollQueryDto dto = response.getBody().getData();

        assertNotNull(dto);
        assertEquals("Enquete Teste", dto.getTitle());
        assertEquals(1, dto.getVoteOptions().size());
        assertEquals("Opção A", dto.getVoteOptions().get(0).getName());
        assertEquals(0, dto.getMyChoices()); // Nenhum voto feito

        System.out.println("====== TESTE: USUÁRIO NÃO LOGADO ======");
        System.out.println("Título: " + dto.getTitle());
        System.out.println("Descrição: " + dto.getDescription());
        System.out.println("myChoices: " + dto.getMyChoices());
        System.out.println("Opções de voto:");
        dto.getVoteOptions().forEach(opt -> System.out.println("- " + opt.getName()));
    }


    @Test
    void deveRetornarEnqueteComVotoDoUsuario() {
        // Usuário logado
        CommonUser user = new CommonUser();
        user.setId(1L);

        // Opções de voto
        VoteOption optionA = new VoteOption();
        optionA.setId(101L);
        optionA.setName("Opção A");

        // Enquete
        Poll poll = new Poll();
        poll.setId(10L);
        poll.setTitle("Enquete Votada");
        poll.setDescription("Descrição");
        poll.setStartDate(LocalDateTime.now());
        poll.setEndDate(LocalDateTime.now().plusDays(1));
        poll.setChoiceLimitPerUser(1);
        poll.setVoteOptions(new ArrayList<>(List.of(optionA)));
        poll.setResponsible(user);

        optionA.setPoll(poll);

        // Voto do usuário
        Vote vote = new Vote();
        vote.setId(500L);
        vote.setVoteOption(optionA);
        vote.setUser(user);

        // Mocks
        when(contextService.getUserOptional()).thenReturn(Optional.of(user));
        when(pollService.findSpecificPoll(10L)).thenReturn(poll);
        when(voteRepository.findUserVotesInPoll(10L, 1L)).thenReturn(List.of(vote));

        // Execução
        ResponseEntity<ApiResponse<PollQueryDto>> response = pollController.getPollById(10L);

        // Verificações
        assertEquals(HttpStatus.OK, response.getStatusCode());

        PollQueryDto dto = response.getBody().getData();
        assertNotNull(dto);
        assertEquals("Enquete Votada", dto.getTitle());
        assertEquals(1, dto.getVoteOptions().size());
        assertEquals("Opção A", dto.getVoteOptions().get(0).getName());
        assertTrue(dto.getMyChoices() > 0); // myChoices diferente de 0

        System.out.println("====== TESTE: USUÁRIO LOGADO ======");
        System.out.println("Título: " + dto.getTitle());
        System.out.println("Descrição: " + dto.getDescription());
        System.out.println("myChoices: " + dto.getMyChoices());
        System.out.println("Opções de voto:");
        dto.getVoteOptions().forEach(opt -> System.out.println("- " + opt.getName()));
    }

    @Test
    void deveRetornarEnqueteSemVotoParaUsuarioLogado() {
        // Usuário logado
        CommonUser user = new CommonUser();
        user.setId(2L);

        // Opção de voto
        VoteOption optionA = new VoteOption();
        optionA.setId(201L);
        optionA.setName("Opção A");

        // Enquete
        Poll poll = new Poll();
        poll.setId(20L);
        poll.setTitle("Enquete Não Votada");
        poll.setDescription("Usuário logado, mas ainda não votou");
        poll.setStartDate(LocalDateTime.now());
        poll.setEndDate(LocalDateTime.now().plusDays(1));
        poll.setChoiceLimitPerUser(1);
        poll.setVoteOptions(new ArrayList<>(List.of(optionA)));
        poll.setResponsible(user);

        optionA.setPoll(poll);

        // Mocks
        when(contextService.getUserOptional()).thenReturn(Optional.of(user));
        when(pollService.findSpecificPoll(20L)).thenReturn(poll);
        when(voteRepository.findUserVotesInPoll(20L, 2L)).thenReturn(List.of()); // ← sem votos

        // Execução
        ResponseEntity<ApiResponse<PollQueryDto>> response = pollController.getPollById(20L);

        // Verificações
        assertEquals(HttpStatus.OK, response.getStatusCode());

        PollQueryDto dto = response.getBody().getData();
        assertNotNull(dto);
        assertEquals("Enquete Não Votada", dto.getTitle());
        assertEquals(1, dto.getVoteOptions().size());
        assertEquals("Opção A", dto.getVoteOptions().get(0).getName());
        assertEquals(0, dto.getMyChoices()); // ← não votou ainda

        // Print simulado para visualização
        System.out.println("====== TESTE: USUÁRIO LOGADO, AINDA NÃO VOTOU ======");
        System.out.println("Título: " + dto.getTitle());
        System.out.println("Descrição: " + dto.getDescription());
        System.out.println("myChoices: " + dto.getMyChoices());
        System.out.println("Opções de voto:");
        dto.getVoteOptions().forEach(opt -> System.out.println("- " + opt.getName()));
    }

    @Test
    void deveRetornarErroQuandoEnqueteNaoExiste() {
        // Mocks
        when(contextService.getUserOptional()).thenReturn(Optional.empty());
        when(pollService.findSpecificPoll(999L)).thenThrow(new RuntimeException("Poll not found"));

        // Execução e verificação
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pollController.getPollById(999L);
        });

        assertEquals("Poll not found", exception.getMessage());

        // Print simulado
        System.out.println("====== TESTE: ENQUETE NÃO ENCONTRADA ======");
        System.out.println("Exceção lançada: " + exception.getClass().getSimpleName());
        System.out.println("Mensagem: " + exception.getMessage());
    }



}
*/