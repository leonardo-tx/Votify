package br.com.votify.api.controller.rest.poll;

import br.com.votify.core.model.poll.*;
import br.com.votify.core.model.poll.field.Description;
import br.com.votify.core.model.poll.field.Title;
import br.com.votify.core.model.poll.field.VoteOptionName;
import br.com.votify.core.model.user.User;
import br.com.votify.core.service.poll.PollService;
import br.com.votify.core.service.user.ContextService;
import br.com.votify.core.service.user.UserService;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.poll.PollInsertDTO;
import br.com.votify.dto.poll.VoteInsertDTO;
import br.com.votify.dto.poll.VoteOptionInsertDTO;
import br.com.votify.test.suites.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class PollControllerTest extends ControllerTest {
    @MockitoBean
    private PollService pollService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ContextService contextService;

    @BeforeEach
    void setupBeforeEach() {
        when(userService.getContext()).thenReturn(contextService);
    }

    @Test
    void testInsertPoll() throws Exception {
        User user = mock(User.class);
        when(user.getId()).thenReturn(3L);
        when(contextService.getUserOrThrow()).thenReturn(user);

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
        when(pollService.createPoll(ArgumentMatchers.any(PollRegister.class), eq(user)))
                .thenReturn(createTestPoll("Test Poll"));

        ResultActions resultActions = mockMvc.perform(post("/api/polls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pollInsertDTO)));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.CREATED)
                .andExpect(jsonPath("data.id", is(14)))
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
    void testGetPollWithoutUserVote() throws Exception {
        User user = mock(User.class);
        Vote vote = mock(Vote.class);
        Poll poll = createTestPoll("Test Poll");
        when(contextService.getUserOptional()).thenReturn(Optional.of(user));
        when(vote.getOption()).thenReturn(0);


        when(pollService.getByIdOrThrow(14L)).thenReturn(poll);
        when(pollService.getVote(poll, user)).thenReturn(vote);

        ResultActions result = mockMvc.perform(get("/api/polls/{id}", 14));
        mockMvcHelper.testSuccessfulResponse(result, HttpStatus.OK)
                .andExpect(jsonPath("data.id", is(14)))
                .andExpect(jsonPath("data.title", is("Test Poll")))
                .andExpect(jsonPath("data.description", is("Test Description")))
                .andExpect(jsonPath("data.voteOptions", hasSize(5)))
                .andExpect(jsonPath("data.votedOption", is(0)));
    }

    @Test
    void testGetPollNotFound() throws Exception {
        long nonExistentPollId = 9999L;
        when(contextService.getUserOptional()).thenReturn(Optional.empty());
        when(pollService.getByIdOrThrow(nonExistentPollId))
                .thenThrow(new VotifyException(VotifyErrorCode.POLL_NOT_FOUND));

        ResultActions result = mockMvc.perform(get("/api/polls/{id}", nonExistentPollId));
        mockMvcHelper.testUnsuccessfulResponse(result, VotifyErrorCode.POLL_NOT_FOUND);
    }

    @Test
    void testGetUserPolls() throws Exception {
        User user = mock(User.class);
        when(userService.getUserById(3L)).thenReturn(user);

        List<Poll> polls = List.of(
                createTestPoll("Test Poll"),
                createTestPoll("Test Poll")
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<Poll> page = new PageImpl<>(polls, pageable, polls.size());
        when(pollService.findAllByUser(user, 0, 10)).thenReturn(page);

        ResultActions resultActions = mockMvc.perform(get("/api/polls/user/{id}", 3));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.pageNumber", is(0)))
                .andExpect(jsonPath("data.pageSize", is(10)))
                .andExpect(jsonPath("data.totalElements", is(2)))
                .andExpect(jsonPath("data.totalPages", is(1)))
                .andExpect(jsonPath("data.first", is(true)))
                .andExpect(jsonPath("data.last", is(true)))
                .andExpect(jsonPath("data.content", hasSize(2)));
    }

    @Test
    void testGetMyPollsWhenAuthenticated() throws Exception {
        User user = mock(User.class);
        when(contextService.getUserOptional()).thenReturn(Optional.of(user));

        List<Poll> polls = List.of(
                createTestPoll("Test Poll"),
                createTestPoll("Test Poll")
        );
        Pageable pageable = PageRequest.of(0, 10);
        Page<Poll> page = new PageImpl<>(polls, pageable, polls.size());
        when(pollService.findAllByUser(user, 0, 10)).thenReturn(page);

        ResultActions resultActions = mockMvc.perform(get("/api/polls/me"));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.pageNumber", is(0)))
                .andExpect(jsonPath("data.pageSize", is(10)))
                .andExpect(jsonPath("data.totalElements", is(2)))
                .andExpect(jsonPath("data.totalPages", is(1)))
                .andExpect(jsonPath("data.first", is(true)))
                .andExpect(jsonPath("data.last", is(true)))
                .andExpect(jsonPath("data.content", hasSize(2)));
    }

    @Test
    void testGetMyPollsWhenNotAuthenticated() throws Exception {
        when(contextService.getUserOptional()).thenReturn(Optional.empty());

        ResultActions resultActions = mockMvc.perform(get("/api/polls/me"));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.pageNumber", is(0)))
                .andExpect(jsonPath("data.pageSize", is(10)))
                .andExpect(jsonPath("data.totalElements", is(0)))
                .andExpect(jsonPath("data.totalPages", is(0)))
                .andExpect(jsonPath("data.first", is(true)))
                .andExpect(jsonPath("data.last", is(true)))
                .andExpect(jsonPath("data.content", hasSize(0)));
    }

    @Test
    void testGetActivePolls() throws Exception {
        List<Poll> polls = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            polls.add(createTestPoll("Test Poll"));
        }

        Pageable pageable = PageRequest.of(0, 10);
        Page<Poll> page = new PageImpl<>(polls, pageable, 12);
        when(pollService.findAllActivePolls(0, 10)).thenReturn(page);

        ResultActions resultActions = mockMvc.perform(get("/api/polls/active"));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.pageNumber", is(0)))
                .andExpect(jsonPath("data.pageSize", is(10)))
                .andExpect(jsonPath("data.totalElements", is(12)))
                .andExpect(jsonPath("data.totalPages", is(2)))
                .andExpect(jsonPath("data.first", is(true)))
                .andExpect(jsonPath("data.last", is(false)))
                .andExpect(jsonPath("data.content", hasSize(10)));
    }

    @Test
    void testGetPollByIdNotAuthenticated() throws Exception {
        when(contextService.getUserOptional()).thenReturn(Optional.empty());
        when(pollService.getByIdOrThrow(14L)).thenReturn(createTestPoll("Test Poll"));

        ResultActions resultActions = mockMvc.perform(get("/api/polls/{id}", 14));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.id", is(14)))
                .andExpect(jsonPath("data.title", is("Test Poll")))
                .andExpect(jsonPath("data.description", is("Test Description")))
                .andExpect(jsonPath("data.voteOptions", hasSize(5)))
                .andExpect(jsonPath("data.voteOptions[0].name", is("Opção 1")))
                .andExpect(jsonPath("data.votedOption", is(0)));
    }

    @Test
    void testSearchPollsWithResults() throws Exception {
        List<Poll> polls = List.of(
                createTestPoll("Qual a"),
                createTestPoll("Qual o"),
                createTestPoll("Qual seu")
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Poll> page = new PageImpl<>(polls, pageable, polls.size());
        when(pollService.findByTitle("Qual", 0, 10)).thenReturn(page);

        ResultActions resultActions = mockMvc.perform(
                get("/api/polls/search")
                        .param("title", "Qual")
                        .param("page", "0")
                        .param("size", "10")
        );

        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.pageNumber", is(0)))
                .andExpect(jsonPath("data.pageSize", is(10)))
                .andExpect(jsonPath("data.totalElements", is(3)))
                .andExpect(jsonPath("data.totalPages", is(1)))
                .andExpect(jsonPath("data.content", hasSize(3)))
                .andExpect(jsonPath("data.content[*].title", everyItem(containsString("Qual"))));
    }

    @Test
    void testSearchPollsWithoutResults() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Poll> page = new PageImpl<>(List.of(), pageable, 0);
        when(pollService.findByTitle("Algo", 0, 10)).thenReturn(page);

        ResultActions resultActions = mockMvc.perform(
                get("/api/polls/search")
                        .param("title", "Algo")
        );

        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.OK)
                .andExpect(jsonPath("data.pageNumber", is(0)))
                .andExpect(jsonPath("data.totalElements", is(0)))
                .andExpect(jsonPath("data.totalPages", is(0)))
                .andExpect(jsonPath("data.content", hasSize(0)));
    }

    @Test
    void testSearchPollsEmptyQuery() throws Exception {
        when(pollService.findByTitle("", 0, 10))
                .thenThrow(new VotifyException(VotifyErrorCode.POLL_TITLE_SEARCH_EMPTY));
        ResultActions resultActions = mockMvc.perform(
                get("/api/polls/search")
                        .param("title", "")
        );
        mockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.POLL_TITLE_SEARCH_EMPTY);
    }

    @Test
    void testVotePoll() throws Exception {
        User user = mock(User.class);
        Poll poll = mock(Poll.class);
        Vote vote = mock(Vote.class);
        when(poll.getVoteOptionsSize()).thenReturn(5);
        when(poll.getChoiceLimitPerUser()).thenReturn(1);
        when(contextService.getUserOrThrow()).thenReturn(user);
        when(vote.getOption()).thenReturn(16);
        when(pollService.getByIdOrThrow(14L)).thenReturn(poll);
        when(pollService.vote(eq(poll), ArgumentMatchers.any(VoteRegister.class))).thenReturn(vote);

        VoteInsertDTO voteInsertDTO = new VoteInsertDTO(16);
        ResultActions resultActions = mockMvc.perform(post("/api/polls/{id}/vote", 14)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteInsertDTO)));
        mockMvcHelper.testSuccessfulResponse(resultActions, HttpStatus.CREATED)
                .andExpect(jsonPath("data", is(16)));
    }

    @Test
    void testInvalidVotePoll() throws Exception {
        User user = mock(User.class);
        Poll poll = mock(Poll.class);
        when(contextService.getUserOrThrow()).thenReturn(user);
        when(pollService.getByIdOrThrow(14L)).thenReturn(poll);
        when(pollService.vote(eq(poll), ArgumentMatchers.any(VoteRegister.class)))
                .thenThrow(new VotifyException(VotifyErrorCode.POLL_VOTE_INVALID));

        VoteInsertDTO voteInsertDTO = new VoteInsertDTO(31);
        ResultActions resultActions = mockMvc.perform(post("/api/polls/{id}/vote", 14)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteInsertDTO)));
        mockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.POLL_VOTE_INVALID);
    }

    @Test
    void testEmptyVotePoll() throws Exception {
        User user = mock(User.class);
        Poll poll = mock(Poll.class);
        when(contextService.getUserOrThrow()).thenReturn(user);
        when(pollService.getByIdOrThrow(14L)).thenReturn(poll);
        when(pollService.vote(eq(poll), ArgumentMatchers.any(VoteRegister.class)))
                .thenThrow(new VotifyException(VotifyErrorCode.POLL_VOTE_EMPTY));

        VoteInsertDTO voteInsertDTO = new VoteInsertDTO(0);
        ResultActions resultActions = mockMvc.perform(post("/api/polls/{id}/vote", 14)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteInsertDTO)));
        mockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.POLL_VOTE_EMPTY);
    }

    @Test
    void testVoteWhenNotAuthenticated() throws Exception {
        when(contextService.getUserOrThrow())
                .thenThrow(new VotifyException(VotifyErrorCode.COMMON_UNAUTHORIZED));

        VoteInsertDTO voteInsertDTO = new VoteInsertDTO(1);
        ResultActions resultActions = mockMvc.perform(post("/api/polls/{id}/vote", 14)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteInsertDTO)));
        mockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.COMMON_UNAUTHORIZED);
    }

    @Test
    void testVotePollDuplicated() throws Exception {
        User user = mock(User.class);
        Poll poll = mock(Poll.class);
        when(poll.getVoteOptionsSize()).thenReturn(5);
        when(poll.getChoiceLimitPerUser()).thenReturn(1);
        when(contextService.getUserOrThrow()).thenReturn(user);
        when(pollService.getByIdOrThrow(14L)).thenReturn(poll);
        when(pollService.vote(eq(poll), ArgumentMatchers.any(VoteRegister.class)))
                .thenThrow(new VotifyException(VotifyErrorCode.POLL_VOTED_ALREADY));

        VoteInsertDTO voteInsertDTO = new VoteInsertDTO(4);
        ResultActions resultActions = mockMvc.perform(post("/api/polls/{id}/vote", 14)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voteInsertDTO)));
        mockMvcHelper.testUnsuccessfulResponse(resultActions, VotifyErrorCode.POLL_VOTED_ALREADY);
    }

    @Test
    void testGetPollWithUserVote() throws Exception {
        User user = mock(User.class);
        Vote vote = mock(Vote.class);
        Poll poll = createTestPoll("Test Poll");
        when(contextService.getUserOptional()).thenReturn(Optional.of(user));
        when(vote.getOption()).thenReturn(16);

        when(pollService.getByIdOrThrow(14L)).thenReturn(poll);
        when(pollService.getVote(poll, user)).thenReturn(vote);

        ResultActions result = mockMvc.perform(get("/api/polls/{id}", 14));
        mockMvcHelper.testSuccessfulResponse(result, HttpStatus.OK)
                .andExpect(jsonPath("data.id", is(14)))
                .andExpect(jsonPath("data.title", is("Test Poll")))
                .andExpect(jsonPath("data.description", is("Test Description")))
                .andExpect(jsonPath("data.voteOptions", hasSize(5)))
                .andExpect(jsonPath("data.votedOption", is(16)));
    }

    @Test
    void testCancelPollAsOwner() throws Exception {
        User user = mock(User.class);
        Poll poll = createTestPoll("Test Poll");
        when(contextService.getUserOrThrow()).thenReturn(user);
        when(pollService.getByIdOrThrow(14L)).thenReturn(poll);

        ResultActions cancelResult = mockMvc.perform(delete("/api/polls/{id}/cancel", 14));
        mockMvcHelper.testSuccessfulResponse(cancelResult, HttpStatus.OK);

        verify(pollService).cancelPoll(poll, user);
    }

    @Test
    void testCancelPollAsNonOwner() throws Exception {
        User user = mock(User.class);
        Poll poll = createTestPoll("Test Poll");
        when(contextService.getUserOrThrow()).thenReturn(user);
        when(pollService.getByIdOrThrow(3L)).thenReturn(poll);
        doThrow(new VotifyException(VotifyErrorCode.POLL_NOT_OWNER))
                .when(pollService)
                .cancelPoll(poll, user);

        ResultActions cancelResult = mockMvc.perform(delete("/api/polls/{id}/cancel", 3));
        mockMvcHelper.testUnsuccessfulResponse(cancelResult, VotifyErrorCode.POLL_NOT_OWNER);
    }

    private Poll createTestPoll(String title) throws VotifyException {

        return Poll.parseUnsafe(
                14L,
                new Title(title),
                new Description("Test Description"),
                Instant.now(),
                Instant.now().plus(Duration.ofDays(1)),
                false,
                List.of(
                        VoteOption.parseUnsafe(new VoteOptionName("Opção 1"), 0, 0, 14L),
                        VoteOption.parseUnsafe(new VoteOptionName("Opção 2"), 0, 1, 14L),
                        VoteOption.parseUnsafe(new VoteOptionName("Opção 3"), 0, 2, 14L),
                        VoteOption.parseUnsafe(new VoteOptionName("Opção 4"), 0, 3, 14L),
                        VoteOption.parseUnsafe(new VoteOptionName("Opção 5"), 0, 4, 14L)
                ),
                1,
                3L
        );
    }
}
