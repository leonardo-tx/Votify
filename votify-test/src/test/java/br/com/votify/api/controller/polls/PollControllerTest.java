package br.com.votify.api.controller.polls;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.dto.polls.PollInsertDTO;
import br.com.votify.dto.polls.VoteInsertDTO;
import br.com.votify.dto.polls.VoteOptionInsertDTO;
import br.com.votify.core.repository.PollRepository;
import br.com.votify.core.repository.UserRepository;
import br.com.votify.core.service.PasswordEncoderService;
import br.com.votify.test.MockMvcHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import br.com.votify.core.domain.entities.polls.Poll;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.domain.entities.users.CommonUser;
import java.time.LocalDateTime;
import java.util.List;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import java.time.ZoneId;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class PollControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoderService passwordEncoderService;



    @Test
    @Order(0)
    public void testInsertPoll() throws Exception {
        List<VoteOptionInsertDTO> voteOptionInsertDTOS = List.of(
                new VoteOptionInsertDTO("Opção 1"),
                new VoteOptionInsertDTO("Opção 2"),
                new VoteOptionInsertDTO("Opção 3"),
                new VoteOptionInsertDTO("Opção 4"),
                new VoteOptionInsertDTO("Opção 5")
        );
        PollInsertDTO pollInsertDTO = new PollInsertDTO(
                "Test Poll",
                "Test Description",
                null,
                LocalDateTime.now().plusDays(1),
                false,
                1,
                voteOptionInsertDTOS
        );

        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "common@votify.com.br", "password123"
        );
        ResultActions resultActions = mockMvc.perform(post("/polls")
                .cookie(cookies)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pollInsertDTO)));
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.CREATED)
                .andExpect(jsonPath("data.id", is(1)))
                .andExpect(jsonPath("data.title", is("Test Poll")))
                .andExpect(jsonPath("data.description", is("Test Description")))
                .andExpect(jsonPath("data.startDate", is(notNullValue())))
                .andExpect(jsonPath("data.endDate", is(notNullValue())))
                .andExpect(jsonPath("data.userRegistration", is(false)))
                .andExpect(jsonPath("data.choiceLimitPerUser", is(1)))
                .andExpect(jsonPath("data.responsibleId", is(3)))
                .andExpect(jsonPath("data.votedOption", is(0)))
                .andExpect(jsonPath("data.voteOptions", hasSize(5)))
                .andExpect(jsonPath("data.voteOptions[*].count", everyItem(is(0))));
    }

    @Test
    @Order(1)
    public void testGetUserPolls() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/polls/user/{id}", 3));
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.pageNumber", is(0)))
                .andExpect(jsonPath("data.pageSize", is(10)))
                .andExpect(jsonPath("data.totalElements", is(1)))
                .andExpect(jsonPath("data.totalPages", is(1)))
                .andExpect(jsonPath("data.first", is(true)))
                .andExpect(jsonPath("data.last", is(true)))
                .andExpect(jsonPath("data.content", hasSize(1)))
                .andExpect(jsonPath("data.content[0].id", is(1)))
                .andExpect(jsonPath("data.content[0].title", is("Test Poll")))
                .andExpect(jsonPath("data.content[0].description", is("Test Description")))
                .andExpect(jsonPath("data.content[0].startDate", is(notNullValue())))
                .andExpect(jsonPath("data.content[0].endDate", is(notNullValue())))
                .andExpect(jsonPath("data.content[0].responsibleId", is(3)));
    }

    @Test
    @Order(1)
    public void testGetMyPollsWhenAuthenticated() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "common@votify.com.br", "password123"
        );

        ResultActions resultActions = mockMvc.perform(get("/polls/me")
                .cookie(cookies));
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.pageNumber", is(0)))
                .andExpect(jsonPath("data.pageSize", is(10)))
                .andExpect(jsonPath("data.totalElements", is(1)))
                .andExpect(jsonPath("data.totalPages", is(1)))
                .andExpect(jsonPath("data.first", is(true)))
                .andExpect(jsonPath("data.last", is(true)))
                .andExpect(jsonPath("data.content", hasSize(1)))
                .andExpect(jsonPath("data.content[0].id", is(1)))
                .andExpect(jsonPath("data.content[0].title", is("Test Poll")))
                .andExpect(jsonPath("data.content[0].description", is("Test Description")))
                .andExpect(jsonPath("data.content[0].startDate", is(notNullValue())))
                .andExpect(jsonPath("data.content[0].endDate", is(notNullValue())))
                .andExpect(jsonPath("data.content[0].responsibleId", is(3)));
    }

    @Test
    @Order(1)
    public void testGetMyPollsWhenNotAuthenticated() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/polls/me"));
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.pageNumber", is(0)))
                .andExpect(jsonPath("data.pageSize", is(10)))
                .andExpect(jsonPath("data.totalElements", is(0)))
                .andExpect(jsonPath("data.totalPages", is(0)))
                .andExpect(jsonPath("data.first", is(true)))
                .andExpect(jsonPath("data.last", is(true)))
                .andExpect(jsonPath("data.content", hasSize(0)));
    }

    @Test
    @Order(1)
    public void testVotePoll() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "common@votify.com.br", "password123"
        );

        VoteInsertDTO voteInsertDTO = new VoteInsertDTO(16);
        ResultActions resultActions = mockMvc.perform(post("/polls/{id}/vote", 1)
                .cookie(cookies)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteInsertDTO)));
        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.CREATED)
                .andExpect(jsonPath("data", is(16)));
    }

    @Test
    @Order(1)
    public void testInvalidVotePoll() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "common@votify.com.br", "password123"
        );

        VoteInsertDTO voteInsertDTO = new VoteInsertDTO(31);
        ResultActions resultActions = mockMvc.perform(post("/polls/{id}/vote", 1)
                .cookie(cookies)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteInsertDTO)));
        MockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.POLL_VOTE_INVALID);
    }

    @Test
    @Order(1)
    public void testEmptyVotePoll() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "common@votify.com.br", "password123"
        );

        VoteInsertDTO voteInsertDTO = new VoteInsertDTO(0);
        ResultActions resultActions = mockMvc.perform(post("/polls/{id}/vote", 1)
                .cookie(cookies)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteInsertDTO)));
        MockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.POLL_VOTE_EMPTY);
    }

    @Test
    @Order(1)
    public void testVoteWhenNotAuthenticated() throws Exception {
        VoteInsertDTO voteInsertDTO = new VoteInsertDTO(1);
        ResultActions resultActions = mockMvc.perform(post("/polls/{id}/vote", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteInsertDTO)));
        MockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.COMMON_UNAUTHORIZED);
    }

    @Test
    @Order(2)
    public void testVotePollDuplicated() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "common@votify.com.br", "password123"
        );

        VoteInsertDTO voteInsertDTO = new VoteInsertDTO(4);
        ResultActions resultActions = mockMvc.perform(post("/polls/{id}/vote", 1)
                .cookie(cookies)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteInsertDTO)));
        MockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.POLL_VOTED_ALREADY);
    }

    @Test
    @Order(3)
    public void testCancelPollAsOwner() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "common@votify.com.br", "password123"
        );
        ResultActions cancelResult = mockMvc.perform(delete("/polls/{id}/cancel", 1)
                .cookie(cookies));
        MockMvcHelper.testSuccessfulResponse(cancelResult, HttpStatus.OK);
    }

    @Test
    @Order(3)
    public void testCancelPollAsNonOwner() throws Exception {
        User nonOwner = new CommonUser(
                null,
                "nonOwner",
                "Non Owner",
                "nonowner@votify.com.br",
                passwordEncoderService.encryptPassword("nonownerpass")
        );
        userRepository.saveAndFlush(nonOwner);

        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "nonowner@votify.com.br", "nonownerpass"
        );
        ResultActions cancelResult = mockMvc.perform(delete("/polls/{id}/cancel", 1)
                .cookie(cookies));
        MockMvcHelper.testUnsuccessfulResponse(cancelResult, VotifyErrorCode.POLL_NOT_OWNER);
    }

    @Test
    @Order(3)
    public void testCancelPollBeforeStartIntegration() throws Exception {
        List<VoteOptionInsertDTO> voteOptions = List.of(
                new VoteOptionInsertDTO("Opção 1"),
                new VoteOptionInsertDTO("Opção 2")
        );
        LocalDateTime futureStart = LocalDateTime.now().plusDays(2);
        LocalDateTime futureEnd = LocalDateTime.now().plusDays(3);
        PollInsertDTO pollInsertDTO = new PollInsertDTO(
                "Future Poll",
                "Poll que ainda não começou",
                futureStart,
                futureEnd,
                false,
                1,
                voteOptions
        );
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "common@votify.com.br", "password123"
        );
        ResultActions createResult = mockMvc.perform(post("/polls")
                .cookie(cookies)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pollInsertDTO)));
        int pollId = MockMvcHelper.extractId(createResult, "$.data.id");
        ResultActions cancelResult = mockMvc.perform(delete("/polls/{id}/cancel", pollId)
                .cookie(cookies));
        MockMvcHelper.testSuccessfulResponse(cancelResult, HttpStatus.OK);
        ResultActions getResult = mockMvc.perform(get("/polls/user/{id}", 3)
                .cookie(cookies));
        getResult.andExpect(jsonPath("data.content[?(@.title=='Future Poll')]").doesNotExist());
    }

    @Test
    @Order(3)
    public void testCancelPollDuringVotingIntegration() throws Exception {
        LocalDateTime nowUtc = LocalDateTime.now(ZoneId.of("UTC"));
        LocalDateTime futureStart = nowUtc.plusMinutes(5);
        LocalDateTime futureEnd = nowUtc.plusDays(1);
        PollInsertDTO pollInsertDTO = new PollInsertDTO(
                "In Progress Poll",
                "Poll em andamento",
                futureStart,
                futureEnd,
                false,
                1,
                List.of(
                        new VoteOptionInsertDTO("Opção 1"),
                        new VoteOptionInsertDTO("Opção 2")
                )
        );
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "common@votify.com.br", "password123"
        );
        ResultActions createResult = mockMvc.perform(post("/polls")
                .cookie(cookies)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pollInsertDTO)));
        int pollId = MockMvcHelper.extractId(createResult, "$.data.id");

        Poll poll = pollRepository.findById((long) pollId).orElseThrow();
        poll.setStartDate(nowUtc.minusHours(1));
        pollRepository.save(poll);

        ResultActions cancelResult = mockMvc.perform(delete("/polls/{id}/cancel", pollId)
                .cookie(cookies));
        MockMvcHelper.testSuccessfulResponse(cancelResult, HttpStatus.OK);

        ResultActions getResult = mockMvc.perform(get("/polls/user/{id}", 3)
                .cookie(cookies));
        getResult.andExpect(jsonPath("data.content[?(@.title=='In Progress Poll')]").exists());
    }


    @Test
    @Order(3)
    public void testCancelPollAfterEndIntegration() throws Exception {
        LocalDateTime nowUtc = LocalDateTime.now(ZoneId.of("UTC"));
        LocalDateTime futureStart = nowUtc.plusMinutes(1);
        LocalDateTime futureEnd = nowUtc.plusDays(1);
        PollInsertDTO pollInsertDTO = new PollInsertDTO(
                "Finished Poll",
                "Poll já finalizada",
                futureStart,
                futureEnd,
                false,
                1,
                List.of(new VoteOptionInsertDTO("Opção 1"))
        );
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "common@votify.com.br", "password123"
        );
        ResultActions createResult = mockMvc.perform(post("/polls")
                .cookie(cookies)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pollInsertDTO)));
        int pollId = MockMvcHelper.extractId(createResult, "$.data.id");

        Poll poll = pollRepository.findById((long) pollId).orElseThrow();
        poll.setStartDate(nowUtc.minusDays(2));
        poll.setEndDate(nowUtc.minusDays(1));
        pollRepository.save(poll);

        ResultActions cancelResult = mockMvc.perform(delete("/polls/{id}/cancel", pollId)
                .cookie(cookies));
        MockMvcHelper.testUnsuccessfulResponse(cancelResult, VotifyErrorCode.POLL_CANNOT_CANCEL_FINISHED);
    }
}
