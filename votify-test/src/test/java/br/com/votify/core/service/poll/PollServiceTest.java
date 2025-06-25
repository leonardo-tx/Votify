package br.com.votify.core.service.poll;

import br.com.votify.core.model.poll.Poll;
import br.com.votify.core.model.poll.PollRegister;
import br.com.votify.core.model.poll.Vote;
import br.com.votify.core.model.poll.VoteRegister;
import br.com.votify.core.model.poll.event.PollUpdateEvent;
import br.com.votify.core.model.user.User;
import br.com.votify.core.repository.poll.PollRepository;
import br.com.votify.core.repository.poll.VoteRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PollServiceTest {

    @Mock
    private PollRepository pollRepository;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private PollService pollService;

    @Test
    void createPoll() {
        PollRegister pollRegister = mock(PollRegister.class);

        User testUser = mock(User.class);
        when(testUser.getId()).thenReturn(2L);

        when(pollRepository.save(any(Poll.class))).thenAnswer(i -> i.getArgument(0));

        Poll createdPoll = assertDoesNotThrow(() -> pollService.createPoll(pollRegister, testUser));
        assertNotNull(createdPoll);
        assertEquals(testUser.getId(), createdPoll.getResponsibleId());
    }

    @Test
    void voteValid() throws VotifyException {
        Poll poll = mock(Poll.class);
        VoteRegister voteRegister = mock(VoteRegister.class);
        Vote vote = mock(Vote.class);

        when(poll.vote(voteRegister)).thenReturn(vote);

        when(pollRepository.save(poll)).thenReturn(poll);
        when(voteRepository.exists(vote)).thenReturn(false);
        when(voteRepository.save(vote)).thenReturn(vote);

        Vote createdVote = assertDoesNotThrow(() -> pollService.vote(poll, voteRegister));
        assertNotNull(createdVote);

        verify(applicationEventPublisher).publishEvent(any(PollUpdateEvent.class));
    }

    @Test
    void voteWithUserAlreadyVoted() throws VotifyException {
        Poll poll = mock(Poll.class);
        VoteRegister voteRegister = mock(VoteRegister.class);
        Vote vote = mock(Vote.class);

        when(poll.vote(voteRegister)).thenReturn(vote);
        when(voteRepository.exists(vote)).thenReturn(true);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> pollService.vote(poll, voteRegister)
        );
        assertEquals(VotifyErrorCode.POLL_VOTED_ALREADY, exception.getErrorCode());
    }

    @Test
    void findAllByUser() throws VotifyException {
        User user = mock(User.class);
        List<Poll> pollResults = List.of(
                mock(Poll.class), mock(Poll.class), mock(Poll.class), mock(Poll.class)
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Poll> pollPage = new PageImpl<>(pollResults, pageable, pollResults.size());

        when(pollRepository.findAllByResponsible(any(User.class), any(Pageable.class))).thenReturn(pollPage);

        Page<Poll> result = pollService.findAllByUser(user, 0, 10);

        assertNotNull(result);
        assertEquals(4, result.getContent().size());
    }

    @Test
    void findAllByUserNegativePageShouldThrow() {
        User user = mock(User.class);
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> pollService.findAllByUser(user, -1, 10)
        );
        assertEquals(VotifyErrorCode.POLL_PAGE_INVALID_PAGE, exception.getErrorCode());
    }

    @Test
    void findAllByUserPageSizeOutOfBoundsShouldThrow() {
        User user = mock(User.class);
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> pollService.findAllByUser(user, 0, 0)
        );
        assertEquals(VotifyErrorCode.POLL_PAGE_LENGTH_INVALID, exception.getErrorCode());
        exception = assertThrows(
                VotifyException.class,
                () -> pollService.findAllByUser(user, 0, PollService.PAGE_SIZE_LIMIT + 1)
        );
        assertEquals(VotifyErrorCode.POLL_PAGE_LENGTH_INVALID, exception.getErrorCode());
    }

    @Test
    void findByTitle() throws VotifyException {
        List<Poll> pollResults = List.of(
                mock(Poll.class), mock(Poll.class), mock(Poll.class), mock(Poll.class)
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Poll> pollPage = new PageImpl<>(pollResults, pageable, pollResults.size());
        
        when(pollRepository.findByTitleContainingIgnoreCase(eq("t"), any(Instant.class), any(Pageable.class))).thenReturn(pollPage);
        
        Page<Poll> result = pollService.findByTitle("t", 0, 10);

        assertNotNull(result);
        assertEquals(4, result.getContent().size());
    }

    @Test
    void findByTitleNegativePageShouldThrow() {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> pollService.findByTitle("t", -1, 10)
        );
        assertEquals(VotifyErrorCode.POLL_PAGE_INVALID_PAGE, exception.getErrorCode());
    }

    @Test
    void findByTitlePageSizeOutOfBoundsShouldThrow() {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> pollService.findByTitle("t", 0, 0)
        );
        assertEquals(VotifyErrorCode.POLL_PAGE_LENGTH_INVALID, exception.getErrorCode());
        exception = assertThrows(
                VotifyException.class,
                () -> pollService.findByTitle("t", 0, PollService.PAGE_SIZE_LIMIT + 1)
        );
        assertEquals(VotifyErrorCode.POLL_PAGE_LENGTH_INVALID, exception.getErrorCode());
    }

    @Test
    void findByTitleIsNullShouldThrow() {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> pollService.findByTitle(null, 0, 10)
        );
        assertEquals(VotifyErrorCode.POLL_TITLE_SEARCH_EMPTY, exception.getErrorCode());
    }

    @Test
    void findByTitleIsBlankShouldThrow() {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> pollService.findByTitle("  ", 0, 10)
        );
        assertEquals(VotifyErrorCode.POLL_TITLE_SEARCH_EMPTY, exception.getErrorCode());
    }

    @Test
    void getByIdOrThrowValidCase() {
        Poll poll = mock(Poll.class);

        when(poll.getId()).thenReturn(2L);
        when(pollRepository.findById(2L)).thenReturn(Optional.of(poll));

        Poll returnedPoll = assertDoesNotThrow(() -> pollService.getByIdOrThrow(2L));
        assertNotNull(returnedPoll);
        assertEquals(2L, poll.getId());
    }

    @Test
    void getByIdOrThrowInvalidCase() {
        when(pollRepository.findById(0L)).thenReturn(Optional.empty());

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> pollService.getByIdOrThrow(0L)
        );
        assertEquals(VotifyErrorCode.POLL_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void findAllActivePolls() throws VotifyException {
        List<Poll> pollResults = List.of(
                mock(Poll.class), mock(Poll.class)
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Poll> activePollsPage = new PageImpl<>(pollResults, pageable, 2);

        when(pollRepository.findAllByActives(any(Instant.class), eq(pageable))).thenReturn(activePollsPage);

        Page<Poll> result = pollService.findAllActivePolls(0, 10);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
    }

    @Test
    void findAllActivePollsNegativePageShouldThrow() {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> pollService.findAllActivePolls(-1, 10)
        );
        assertEquals(VotifyErrorCode.POLL_PAGE_INVALID_PAGE, exception.getErrorCode());
    }

    @Test
    void findAllActivePollsPageSizeOutOfBoundsShouldThrow() {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> pollService.findAllActivePolls(0, 0)
        );
        assertEquals(VotifyErrorCode.POLL_PAGE_LENGTH_INVALID, exception.getErrorCode());
        exception = assertThrows(
                VotifyException.class,
                () -> pollService.findAllActivePolls(0, PollService.PAGE_SIZE_LIMIT + 1)
        );
        assertEquals(VotifyErrorCode.POLL_PAGE_LENGTH_INVALID, exception.getErrorCode());
    }

    @Test
    void getVoteFound() {
        Poll poll = mock(Poll.class);
        User user = mock(User.class);
        Vote vote = mock(Vote.class);

        when(voteRepository.findByPollAndUser(poll, user)).thenReturn(Optional.of(vote));

        Vote returnedVote = pollService.getVote(poll, user);
        assertEquals(vote, returnedVote);
    }

    @Test
    void getVoteNotFound() {
        Poll poll = mock(Poll.class);
        User user = mock(User.class);

        when(poll.getId()).thenReturn(1L);
        when(user.getId()).thenReturn(4L);

        when(voteRepository.findByPollAndUser(poll, user)).thenReturn(Optional.empty());

        Vote vote = pollService.getVote(poll, user);

        assertEquals(0, vote.getOption());
        assertEquals(poll.getId(), vote.getPollId());
        assertEquals(user.getId(), vote.getUserId());
    }

    @Test
    void cancelActivePollSuccess() throws VotifyException {
        Poll poll = mock(Poll.class);
        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(poll.getResponsibleId()).thenReturn(1L);
        when(poll.hasNotStarted()).thenReturn(false);

        assertDoesNotThrow(() -> pollService.cancelPoll(poll, user));

        verify(pollRepository).save(poll);
        verify(poll).cancel();
    }

    @Test
    void cancelNotStartedPollSuccess() throws VotifyException {
        Poll poll = mock(Poll.class);
        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(poll.getResponsibleId()).thenReturn(1L);
        when(poll.hasNotStarted()).thenReturn(true);

        assertDoesNotThrow(() -> pollService.cancelPoll(poll, user));

        verify(pollRepository).delete(poll);
        verify(poll).cancel();
    }

    @Test
    void cancelNotOwnerFail() throws VotifyException {
        Poll poll = mock(Poll.class);
        User user = mock(User.class);

        when(user.getId()).thenReturn(1L);
        when(poll.getResponsibleId()).thenReturn(2L);

        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> pollService.cancelPoll(poll, user)
        );
        assertEquals(VotifyErrorCode.POLL_NOT_OWNER, exception.getErrorCode());

        verify(pollRepository, never()).delete(poll);
        verify(pollRepository, never()).save(poll);
        verify(poll, never()).cancel();
    }

    @Test
    void deletePollInfoFromUserWithNoPollOrVote() {
        User activeUser = mock(User.class);

        when(voteRepository.findAllFromUser(activeUser)).thenReturn(List.of());
        when(pollRepository.findAllByResponsible(activeUser)).thenReturn(List.of());

        pollService.deletePollInfoFromUser(activeUser);

        verifyNoMoreInteractions(voteRepository);
        verifyNoMoreInteractions(pollRepository);
    }

    @Test
    void deletePollInfoFromUserWithVotes() {
        User activeUser = mock(User.class);
        Poll expiredPoll = mock(Poll.class);
        Poll activePoll = mock(Poll.class);
        Vote voteFromExpiredPoll = mock(Vote.class);
        Vote voteFromActivePoll = mock(Vote.class);

        when(expiredPoll.hasEnded()).thenReturn(true);
        when(activePoll.hasEnded()).thenReturn(false);
        when(voteFromExpiredPoll.getPollId()).thenReturn(1L);
        when(voteFromActivePoll.getPollId()).thenReturn(2L);

        when(voteRepository.findAllFromUser(activeUser)).thenReturn(List.of(voteFromActivePoll, voteFromExpiredPoll));
        when(pollRepository.findAllByResponsible(activeUser)).thenReturn(List.of());
        when(pollRepository.findById(1L)).thenReturn(Optional.of(expiredPoll));
        when(pollRepository.findById(2L)).thenReturn(Optional.of(activePoll));

        pollService.deletePollInfoFromUser(activeUser);

        verify(activePoll).removeVote(voteFromActivePoll);
        verify(expiredPoll, never()).removeVote(voteFromExpiredPoll);
        verify(voteRepository).deleteAllByUser(activeUser);
        verify(pollRepository).save(activePoll);
    }

    @Test
    void deletePollInfoFromUserWithPolls() {
        User activeUser = mock(User.class);
        Poll expiredPoll = mock(Poll.class);
        Poll activePoll = mock(Poll.class);

        when(expiredPoll.hasEnded()).thenReturn(true);
        when(activePoll.hasEnded()).thenReturn(false);

        when(voteRepository.findAllFromUser(activeUser)).thenReturn(List.of());
        when(pollRepository.findAllByResponsible(activeUser)).thenReturn(
                List.of(expiredPoll, activePoll)
        );

        pollService.deletePollInfoFromUser(activeUser);

        verifyNoMoreInteractions(voteRepository);
        verify(pollRepository).delete(activePoll);
        verify(pollRepository, never()).delete(expiredPoll);
    }
}