package br.com.votify.core.service;

import br.com.votify.core.domain.entities.poll.Poll;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.domain.entities.vote.VoteOption;
import br.com.votify.core.repository.PollRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PollServiceTest {
    private static final List<Poll> polls = new ArrayList<>();
    private static PollService pollService;
    private static Long entityId = 1L;
    private static final LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC")).plusDays(1);
    private static final LocalDateTime futureDate = now.plusDays(5);

    @BeforeEach
    void prepareBeforeEach() {
        PollRepository pollRepository = mock(PollRepository.class);

        when(pollRepository.existsByTitleAndResponsibleId(any(String.class), any(Long.class)))
                .thenAnswer((invocation) -> {
                    String title = invocation.getArgument(0);
                    Long userId = invocation.getArgument(1);
                    return polls.stream().anyMatch(p -> p.getTitle().equals(title) && p.getResponsible().getId().equals(userId));
                });

        when(pollRepository.save(any(Poll.class))).thenAnswer((invocation) -> {
            Poll createdPoll = invocation.getArgument(0);
            createdPoll.setId(entityId++);
            polls.add(createdPoll);
            return createdPoll;
        });

        pollService = new PollService(pollRepository);
    }

    @Test
    @Order(0)
    void createValidPoll() {

        User user = new CommonUser(1L, "valid-user", "Valid User", "valid@user.com", "password");
        Poll poll = new Poll("Title", "Description", now, futureDate, true, List.of(new VoteOption(), new VoteOption()), 2);

        Poll pollFromService = assertDoesNotThrow(() -> pollService.createPoll(poll, user));
        assertEquals(1, pollFromService.getId());
    }

    @Test
    @Order(1)
    void createPollWithDuplicateTitle() {
        User user = new CommonUser(1L, "valid-user", "Valid User", "valid@user.com", "password");
        Poll poll = new Poll("Title", "Description", now, futureDate, true, List.of(new VoteOption(), new VoteOption()),2);

        VotifyException exception = assertThrows(VotifyException.class, () -> pollService.createPoll(poll, user));
        assertEquals(VotifyErrorCode.POLL_TITLE_ALREADY_EXISTS_FOR_THIS_USER, exception.getErrorCode());
    }

    @Test
    @Order(2)
    void createPollWithEmptyTitle() {
        User user = new CommonUser(1L, "valid-user", "Valid User", "valid@user.com", "password");
        Poll poll = new Poll("", "Description", now, futureDate, true, List.of(new VoteOption(), new VoteOption()), 2);

        VotifyException exception = assertThrows(VotifyException.class, () -> pollService.createPoll(poll, user));
        assertEquals(VotifyErrorCode.POLL_TITLE_INVALID_LENGTH, exception.getErrorCode());
    }
}