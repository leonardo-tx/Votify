package br.com.votify.api.controller.polls;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.dto.polls.PollInsertDTO;
import br.com.votify.dto.polls.VoteInsertDTO;
import br.com.votify.dto.polls.VoteOptionInsertDTO;
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

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class PollControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                Instant.now().plus(Duration.ofDays(1)),
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
    public void testGetPollWithoutUserVote() throws Exception {
        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "common@votify.com.br", "password123"
        );

        ResultActions result = mockMvc.perform(get("/polls/{id}", 1).cookie(cookies));

        MockMvcHelper.testSuccessfulResponse(result, HttpStatus.OK)
                .andExpect(jsonPath("data.id", is(1)))
                .andExpect(jsonPath("data.title", is("Test Poll")))
                .andExpect(jsonPath("data.description", is("Test Description")))
                .andExpect(jsonPath("data.voteOptions", hasSize(5)))
                .andExpect(jsonPath("data.voteOptions[0].name", is("Opção 1")))
                .andExpect(jsonPath("data.myChoices", is(0)));
    }

    @Test
    @Order(1)
    public void testGetPollNotFound() throws Exception {
        long nonExistentPollId = 9999L;

        ResultActions resultActions = mockMvc.perform(get("/polls/{id}", nonExistentPollId));

        MockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.POLL_NOT_FOUND);
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
    public void testGetPollByIdNotAuthenticated() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/polls/{id}", 1));

        MockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.id", is(1)))
                .andExpect(jsonPath("data.title", is("Test Poll")))
                .andExpect(jsonPath("data.description", is("Test Description")))
                .andExpect(jsonPath("data.voteOptions", hasSize(5)))
                .andExpect(jsonPath("data.voteOptions[0].name", is("Opção 1")))
                .andExpect(jsonPath("data.myChoices", is(0)));
    }

    @Test
    @Order(1)
    public void testSearchPollsWithResults() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                get("/polls/search")
                        .param("title", "Test")
                        .param("page", "0")
                        .param("size", "10")
        );

        MockMvcHelper
                .testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.pageNumber", is(0)))
                .andExpect(jsonPath("data.pageSize", is(10)))
                .andExpect(jsonPath("data.totalElements", is(1)))
                .andExpect(jsonPath("data.totalPages", is(1)))
                .andExpect(jsonPath("data.content", hasSize(1)))
                .andExpect(jsonPath("data.content[0].id", is(1)))
                .andExpect(jsonPath("data.content[0].title", containsString("Test")))
                .andExpect(jsonPath("data.content[0].description", containsString("Test")));
    }

    @Test
    @Order(1)
    public void testSearchPollsWithoutResults() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                get("/polls/search")
                        .param("title", "Algo")
        );

        MockMvcHelper
                .testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.pageNumber", is(0)))
                .andExpect(jsonPath("data.totalElements", is(0)))
                .andExpect(jsonPath("data.totalPages", is(0)))
                .andExpect(jsonPath("data.content", hasSize(0)));
    }

    @Test
    @Order(1)
    public void testSearchPollsEmptyQuery() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                get("/polls/search")
                        .param("title", "")
        );
        MockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.POLL_TITLE_SEARCH_EMPTY);
    }

    @Test
    @Order(1)
    public void testSearchPollsTestQuery() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                get("/polls/search")
                        .param("title", "Test")
        );

        MockMvcHelper
                .testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.pageNumber", is(0)))
                .andExpect(jsonPath("data.pageSize", is(10)))
                .andExpect(jsonPath("data.totalElements", is(1)))
                .andExpect(jsonPath("data.totalPages", is(1)))
                .andExpect(jsonPath("data.content", hasSize(1)))
                .andExpect(jsonPath("data.content[0].id", is(1)))
                .andExpect(jsonPath("data.content[0].title", containsString("Test")))
                .andExpect(jsonPath("data.content[0].description", containsString("Test")));
    }

    @Test
    @Order(2)
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
    @Order(2)
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
    @Order(2)
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
    @Order(2)
    public void testVoteWhenNotAuthenticated() throws Exception {
        VoteInsertDTO voteInsertDTO = new VoteInsertDTO(1);
        ResultActions resultActions = mockMvc.perform(post("/polls/{id}/vote", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteInsertDTO)));
        MockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.COMMON_UNAUTHORIZED);
    }

    @Test
    @Order(3)
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
    public void testGetPollWithUserVote() throws Exception {

        Cookie[] cookies = MockMvcHelper.login(
                mockMvc, objectMapper, "common@votify.com.br", "password123"
        );

        ResultActions result = mockMvc.perform(get("/polls/{id}", 1)
                .cookie(cookies));

        MockMvcHelper.testSuccessfulResponse(result, HttpStatus.OK)
                .andExpect(jsonPath("data.id", is(1)))
                .andExpect(jsonPath("data.title", is("Test Poll")))
                .andExpect(jsonPath("data.description", is("Test Description")))
                .andExpect(jsonPath("data.voteOptions", hasSize(5)))
                .andExpect(jsonPath("data.voteOptions[0].name", is("Opção 1")))
                .andExpect(jsonPath("data.myChoices", greaterThan(0)));
    }
}
