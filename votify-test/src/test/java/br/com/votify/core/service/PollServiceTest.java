package br.com.votify.core.service;


import br.com.votify.core.pollTemp.*;
import br.com.votify.dto.polls.PollQueryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PollServiceTest {
    @Mock
    private PollRepository pollRepository;

    @Mock
    private VoteRepository voteRepository;

    @InjectMocks
    private PollService pollService;

    private Poll pollMock;
    private Vote voteMock;

    @BeforeEach
    void setup() {
        pollMock = new Poll();
        pollMock.setId(1L);
        pollMock.setQuestion("Test Question");
        pollMock.setDescription("Test Description");

        voteMock = new Vote();
        voteMock.setId(10L);
        voteMock.setUserId(100L);
        voteMock.setChosenOption("Option A");
        voteMock.setPoll(pollMock);
    }

    @Test
    void shouldReturnPollWithUserVote() {
        when(pollRepository.findById(1L)).thenReturn(Optional.of(pollMock));
        when(voteRepository.findByPollIdAndUserId(1L, 100L)).thenReturn(Optional.of(voteMock));

        PollQueryDto result = pollService.findSpecificPoll(1L, 100L);

        assertNotNull(result);
        assertEquals("Test Question", result.getQuestion());
        assertEquals("Option A", result.getUserVote());
        verify(pollRepository, times(1)).findById(1L);
        verify(voteRepository, times(1)).findByPollIdAndUserId(1L, 100L);
    }

    @Test
    void shouldReturnNoVoteWhenUserDidNotVote() {
        when(pollRepository.findById(1L)).thenReturn(Optional.of(pollMock));
        when(voteRepository.findByPollIdAndUserId(1L, 200L)).thenReturn(Optional.empty());

        PollQueryDto result = pollService.findSpecificPoll(1L, 200L);

        assertNotNull(result);
        assertEquals("Test Question", result.getQuestion());
        assertEquals("no vote", result.getUserVote());
    }

    @Test
    void shouldThrowExceptionIfPollNotFound() {
        when(pollRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                pollService.findSpecificPoll(99L, null)
        );
    }

    @Test
    void shouldReturnNoVoteForNullUser() {
        when(pollRepository.findById(1L)).thenReturn(Optional.of(pollMock));

        PollQueryDto result = pollService.findSpecificPoll(1L, null);

        assertNotNull(result);
        assertEquals("no vote", result.getUserVote());
        verify(voteRepository,  never()).findByPollIdAndUserId(anyLong(), anyLong());
    }
}