package br.com.votify.api.controller.poll;

import br.com.votify.core.domain.entities.poll.Poll;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.service.ContextService;
import br.com.votify.core.service.PollService;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.PageResponse;
import br.com.votify.dto.poll.PollDetailedViewDTO;
import br.com.votify.dto.poll.PollInsertDTO;
import br.com.votify.dto.poll.PollListViewDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class PollControllerTest {

    @Mock
    private PollService pollService;

    @Mock
    private ContextService contextService;

    @InjectMocks
    private PollController pollController;

    private User testUser;
    private Poll testPoll;
    private List<Poll> testPolls;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testUser = new CommonUser(1L, "testuser", "Test User", "test@example.com", "password123");
        
        testPoll = new Poll(
            "Test Poll",
            "Test Description",
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            false,
            new ArrayList<>(),
            1
        );
        testPoll.setId(1L);
        testPoll.setResponsible(testUser);
        
        testPolls = new ArrayList<>();
        testPolls.add(testPoll);
        
        Poll testPoll2 = new Poll(
            "Test Poll 2",
            "Test Description 2",
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(2),
            false,
            new ArrayList<>(),
            1
        );
        testPoll2.setId(2L);
        testPoll2.setResponsible(testUser);
        testPolls.add(testPoll2);
    }

    @Test
    public void testInsertPoll() throws Exception {
        PollInsertDTO pollInsertDTO = new PollInsertDTO();
        pollInsertDTO.setTitle("Test Poll");
        pollInsertDTO.setDescription("Test Description");
        pollInsertDTO.setStartDate(LocalDateTime.now());
        pollInsertDTO.setEndDate(LocalDateTime.now().plusDays(1));
        pollInsertDTO.setUserRegistration(false);
        pollInsertDTO.setChoiceLimitPerUser(1);
        pollInsertDTO.setVoteOptions(new ArrayList<>());

        when(contextService.getUserOrThrow()).thenReturn(testUser);
        when(pollService.createPoll(any(Poll.class), eq(testUser))).thenReturn(testPoll);

        ResponseEntity<ApiResponse<PollDetailedViewDTO>> response = pollController.insertPoll(pollInsertDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals(1L, response.getBody().getData().getId());
        assertEquals("Test Poll", response.getBody().getData().getTitle());
    }
    
    @Test
    public void testGetUserPolls() throws VotifyException {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Poll> pollPage = new PageImpl<>(testPolls, pageable, testPolls.size());
        
        when(pollService.findAllByUserId(eq(1L), any(Integer.class), any(Integer.class))).thenReturn(pollPage);
        
        ResponseEntity<ApiResponse<PageResponse<PollListViewDTO>>> response = 
            pollController.getUserPolls(1L, page, size);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().getContent().size());
        assertEquals(1L, response.getBody().getData().getContent().get(0).getId());
        assertEquals("Test Poll", response.getBody().getData().getContent().get(0).getTitle());
        assertEquals(2L, response.getBody().getData().getContent().get(1).getId());
        assertEquals("Test Poll 2", response.getBody().getData().getContent().get(1).getTitle());
    }
    
    @Test
    public void testGetMyPollsWhenAuthenticated() throws VotifyException {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Poll> pollPage = new PageImpl<>(testPolls, pageable, testPolls.size());
        
        when(contextService.getUserOptional()).thenReturn(Optional.of(testUser));
        when(pollService.findAllByUserId(eq(1L), any(Integer.class), any(Integer.class))).thenReturn(pollPage);
        
        ResponseEntity<ApiResponse<PageResponse<PollListViewDTO>>> response = 
            pollController.getMyPolls(page, size);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals(2, response.getBody().getData().getContent().size());
        assertEquals(1L, response.getBody().getData().getContent().get(0).getId());
        assertEquals("Test Poll", response.getBody().getData().getContent().get(0).getTitle());
        assertEquals(2L, response.getBody().getData().getContent().get(1).getId());
        assertEquals("Test Poll 2", response.getBody().getData().getContent().get(1).getTitle());
    }
    
    @Test
    public void testGetMyPollsWhenNotAuthenticated() throws VotifyException {
        int page = 0;
        int size = 10;
        
        when(contextService.getUserOptional()).thenReturn(Optional.empty());
        
        ResponseEntity<ApiResponse<PageResponse<PollListViewDTO>>> response = 
            pollController.getMyPolls(page, size);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().isSuccess());
        assertEquals(0, response.getBody().getData().getContent().size());
        assertEquals(0, response.getBody().getData().getTotalElements());
    }
}
