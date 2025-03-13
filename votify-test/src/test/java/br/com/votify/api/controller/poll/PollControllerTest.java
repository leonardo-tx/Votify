package br.com.votify.api.controller.poll;

import br.com.votify.core.domain.entities.poll.Poll;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.service.ContextService;
import br.com.votify.core.service.PollService;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.poll.PollDetailedViewDTO;
import br.com.votify.dto.poll.PollInsertDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PollControllerTest {

    @InjectMocks
    private PollController pollController;

    @Mock
    private PollService pollService;

    @Mock
    private ContextService contextService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void insertPoll_shouldReturnCreatedStatus_whenPollIsValid() throws VotifyException {
        CommonUser user = new CommonUser();
        PollInsertDTO pollInsertDTO = new PollInsertDTO();
        Poll poll = new Poll();
        poll.setResponsible(user);
        when(contextService.getUserOrThrow()).thenReturn(user);
        when(pollService.createPoll(any(Poll.class), any())).thenReturn(poll);

        ResponseEntity<ApiResponse<PollDetailedViewDTO>> response = pollController.insertPoll(pollInsertDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        verify(pollService, times(1)).createPoll(any(Poll.class), any());
    }

    @Test
    void insertPoll_shouldThrowException_whenPollIsInvalid() throws VotifyException {
        PollInsertDTO pollInsertDTO = new PollInsertDTO();
        when(contextService.getUserOrThrow()).thenReturn(new CommonUser());
        when(pollService.createPoll(any(Poll.class), any())).thenThrow(new VotifyException(VotifyErrorCode.POLL_TITLE_INVALID_LENGTH));

        assertThrows(VotifyException.class,   () -> pollController.insertPoll(pollInsertDTO));
        verify(pollService, times(1)).createPoll(any(Poll.class), any());
    }
}
