package br.com.votify.core.service;

import br.com.votify.core.domain.entities.polls.*;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.PollRepository;
import br.com.votify.core.repository.VoteRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.ZoneId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PollServiceTest {

    @Mock
    private PollRepository pollRepository;

    @Mock
    private VoteRepository voteRepository;

    @InjectMocks
    private PollService pollService;

    private final List<Poll> testPolls = new ArrayList<>();
    private User testUser;

    @BeforeEach
    public void setupBeforeEach() {
        testUser = new CommonUser(1L, "testuser", "Test User", "test@example.com", "password123");
        // Poll sem datas definidas (será tratada em createPoll)
        Poll testPoll = Poll.builder()
                .id(1L)
                .title("Test Poll")
                .description("Test Description")
                .endDate(LocalDateTime.now().plusDays(1))
                .userRegistration(false)
                .voteOptions(List.of(
                        new VoteOption(new VoteOptionIdentifier(1L, 0), "Option 1", 0, null),
                        new VoteOption(new VoteOptionIdentifier(1L, 1), "Option 2", 0, null),
                        new VoteOption(new VoteOptionIdentifier(1L, 2), "Option 3", 0, null)
                ))
                .choiceLimitPerUser(1)
                .responsible(testUser)
                .build();
        testPolls.add(testPoll);

        // Outros polls para testes de votação e listagem...
        Poll testPoll2 = Poll.builder()
                .id(2L)
                .title("Test Poll 2")
                .description("Test Description 2")
                .endDate(LocalDateTime.now().plusDays(2))
                .userRegistration(false)
                .voteOptions(List.of(
                        new VoteOption(new VoteOptionIdentifier(2L, 0), "Option 1", 0, null),
                        new VoteOption(new VoteOptionIdentifier(2L, 1), "Option 2", 0, null),
                        new VoteOption(new VoteOptionIdentifier(2L, 2), "Option 3", 0, null),
                        new VoteOption(new VoteOptionIdentifier(2L, 3), "Option 4", 0, null),
                        new VoteOption(new VoteOptionIdentifier(2L, 4), "Option 5", 0, null)
                ))
                .choiceLimitPerUser(1)
                .responsible(testUser)
                .build();
        testPolls.add(testPoll2);

        Poll testPoll3 = Poll.builder()
                .id(3L)
                .title("Test Poll 3")
                .description("Test Description 3")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .userRegistration(true)
                .voteOptions(List.of(
                        new VoteOption(new VoteOptionIdentifier(3L, 0), "Option 1", 0, null),
                        new VoteOption(new VoteOptionIdentifier(3L, 1), "Option 2", 0, null),
                        new VoteOption(new VoteOptionIdentifier(3L, 2), "Option 3", 0, null),
                        new VoteOption(new VoteOptionIdentifier(3L, 3), "Option 4", 0, null),
                        new VoteOption(new VoteOptionIdentifier(3L, 4), "Option 5", 0, null)
                ))
                .choiceLimitPerUser(3)
                .responsible(testUser)
                .build();
        testPolls.add(testPoll3);

        Poll testPoll4 = Poll.builder()
                .id(4L)
                .title("Test Poll 4")
                .description("Test Description 4")
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now().minusDays(1))
                .userRegistration(false)
                .voteOptions(List.of(
                        new VoteOption(new VoteOptionIdentifier(4L, 0), "Option 1", 15, null),
                        new VoteOption(new VoteOptionIdentifier(4L, 1), "Option 2", 2, null),
                        new VoteOption(new VoteOptionIdentifier(4L, 2), "Option 3", 99, null)
                ))
                .choiceLimitPerUser(3)
                .responsible(testUser)
                .build();
        testPolls.add(testPoll4);
    }

    @Test
    public void createPollWithEmptyTitle() {
        Poll poll = testPolls.get(0);
        poll.setTitle("");

        VotifyException exception = assertThrows(VotifyException.class, () -> pollService.createPoll(poll, testUser));
        assertEquals(VotifyErrorCode.POLL_TITLE_INVALID_LENGTH, exception.getErrorCode());
    }

    @Test
    public void createValidPoll() {
        Poll poll = testPolls.get(0);
        when(pollRepository.save(poll)).thenReturn(poll);

        Poll createdPoll = assertDoesNotThrow(() -> pollService.createPoll(poll, testUser));
        assertNotNull(createdPoll);
    }

    @Test
    public void createValidPollWithStartDate() {
        Poll poll = testPolls.get(2);
        when(pollRepository.save(poll)).thenReturn(poll);

        Poll createdPoll = assertDoesNotThrow(() -> pollService.createPoll(poll, testUser));
        assertNotNull(createdPoll);
    }

    @Test
    public void voteValid() {
        Vote vote = new Vote(null, 4, null, null);
        Poll poll = testPolls.get(0);
        poll.setStartDate(LocalDateTime.now());

        VoteIdentifier voteId = new VoteIdentifier(poll.getId(), testUser.getId());

        when(voteRepository.existsById(voteId)).thenReturn(false);
        when(voteRepository.save(vote)).thenReturn(vote);

        Vote createdVote = assertDoesNotThrow(() -> pollService.vote(vote, poll, testUser));
        assertEquals(new VoteIdentifier(poll.getId(), testUser.getId()), createdVote.getId());
        // Verifica se o count da opção votada (neste exemplo, opção com índice 2) foi incrementado
        assertEquals(1, poll.getVoteOptions().get(2).getCount());
    }

    @Test
    public void voteWithUserAlreadyVoted() {
        Vote vote = new Vote(null, 4, null, null);
        Poll poll = testPolls.get(0);
        poll.setStartDate(LocalDateTime.now());

        VoteIdentifier voteId = new VoteIdentifier(poll.getId(), testUser.getId());

        when(voteRepository.existsById(voteId)).thenReturn(true);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> pollService.vote(vote, poll, testUser)
        );
        assertEquals(VotifyErrorCode.POLL_VOTED_ALREADY, exception.getErrorCode());
    }

    @Test
    public void voteWithInvalidVote() {
        Vote vote = new Vote(null, 64, null, null);
        Poll poll = testPolls.get(1);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> pollService.vote(vote, poll, testUser)
        );
        assertEquals(VotifyErrorCode.POLL_VOTE_INVALID, exception.getErrorCode());
    }

    @Test
    public void voteBeforeStart() {
        Vote vote = new Vote(null, 7, null, null);
        Poll poll = testPolls.get(2);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> pollService.vote(vote, poll, testUser)
        );
        assertEquals(VotifyErrorCode.POLL_VOTE_OUT_OF_DATE_INTERVAL, exception.getErrorCode());
    }

    @Test
    public void voteAfterEnd() {
        Vote vote = new Vote(null, 7, null, null);
        Poll poll = testPolls.get(3);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> pollService.vote(vote, poll, testUser)
        );
        assertEquals(VotifyErrorCode.POLL_VOTE_OUT_OF_DATE_INTERVAL, exception.getErrorCode());
    }

    @Test
    public void findAllByUserId() throws VotifyException {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Poll> pollPage = new PageImpl<>(testPolls, pageable, testPolls.size());

        when(pollRepository.findAllByResponsibleId(eq(1L), any(Pageable.class))).thenReturn(pollPage);

        Page<Poll> result = pollService.findAllByUserId(1L, 0, 10);

        assertNotNull(result);
        assertEquals(4, result.getContent().size());
    }

    @Test
    public void getByIdOrThrowValidCase() {
        when(pollRepository.findById(2L)).thenReturn(Optional.of(testPolls.get(1)));

        Poll poll = assertDoesNotThrow(() -> pollService.getByIdOrThrow(2L));
        assertNotNull(poll);
        assertEquals(2L, poll.getId());
    }

    @Test
    public void getByIdOrThrowInvalidCase() {
        when(pollRepository.findById(0L)).thenReturn(Optional.empty());

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> pollService.getByIdOrThrow(0L)
        );
        assertEquals(VotifyErrorCode.POLL_NOT_FOUND, exception.getErrorCode());
    }


    @Test
    public void cancelPollBeforeStart() {
        Poll poll = Poll.builder()
                .id(10L)
                .title("Future Poll")
                .description("Poll que não iniciou")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .userRegistration(false)
                .voteOptions(List.of(new VoteOption(new VoteOptionIdentifier(10L, 0), "Option 1", 0, null)))
                .choiceLimitPerUser(1)
                .responsible(testUser)
                .build();
        when(pollRepository.findById(10L)).thenReturn(Optional.of(poll));

        assertDoesNotThrow(() -> pollService.cancelPoll(10L, testUser));
        verify(pollRepository).delete(poll);
    }

    @Test
    public void cancelPollDuringVoting() {
        LocalDateTime nowUtc = LocalDateTime.now(ZoneId.of("UTC"));
        Poll poll = Poll.builder()
                .id(11L)
                .title("In Progress Poll")
                .description("Poll em andamento")
                .startDate(nowUtc.minusHours(1))
                .endDate(nowUtc.plusHours(1))
                .userRegistration(false)
                .voteOptions(List.of(new VoteOption(new VoteOptionIdentifier(11L, 0), "Option 1", 0, null)))
                .choiceLimitPerUser(1)
                .responsible(testUser)
                .build();
        when(pollRepository.findById(11L)).thenReturn(Optional.of(poll));

        assertDoesNotThrow(() -> pollService.cancelPoll(11L, testUser));
        assertTrue(poll.isArchived());
        verify(pollRepository).save(poll);
    }

    @Test
    public void cancelPollByNotOwner() {
        Poll poll = Poll.builder()
                .id(12L)
                .title("Poll of TestUser")
                .description("Poll de testUser")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(2))
                .userRegistration(false)
                .voteOptions(List.of(new VoteOption(new VoteOptionIdentifier(12L, 0), "Option 1", 0, null)))
                .choiceLimitPerUser(1)
                .responsible(testUser)
                .build();
        when(pollRepository.findById(12L)).thenReturn(Optional.of(poll));
        User otherUser = new CommonUser(2L, "otheruser", "Other User", "other@example.com", "password456");

        VotifyException exception = assertThrows(VotifyException.class, () -> pollService.cancelPoll(12L, otherUser));
        assertEquals(VotifyErrorCode.POLL_NOT_OWNER, exception.getErrorCode());
    }

    @Test
    public void cancelPollAfterEnd() {
        Poll poll = Poll.builder()
                .id(13L)
                .title("Finished Poll")
                .description("Poll finalizada")
                .startDate(LocalDateTime.now().minusDays(2))
                .endDate(LocalDateTime.now().minusDays(1))
                .userRegistration(false)
                .voteOptions(List.of(new VoteOption(new VoteOptionIdentifier(13L, 0), "Option 1", 0, null)))
                .choiceLimitPerUser(1)
                .responsible(testUser)
                .build();
        when(pollRepository.findById(13L)).thenReturn(Optional.of(poll));

        VotifyException exception = assertThrows(VotifyException.class, () -> pollService.cancelPoll(13L, testUser));
        assertEquals(VotifyErrorCode.POLL_CANNOT_CANCEL_FINISHED, exception.getErrorCode());
    }
}
