package br.com.votify.core.service;

import br.com.votify.core.domain.entities.poll.Poll;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.PollRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class PollServiceTest {

    @Mock
    private PollRepository pollRepository;

    @InjectMocks
    private PollService pollService;

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
    public void testFindAllByUserId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Poll> pollPage = new PageImpl<>(testPolls, pageable, testPolls.size());
        
        when(pollRepository.findAllByResponsibleId(eq(1L), any(Pageable.class))).thenReturn(pollPage);
        
        Page<Poll> result = pollService.findAllByUserId(1L, pageable);
        
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals("Test Poll", result.getContent().get(0).getTitle());
        assertEquals(2L, result.getContent().get(1).getId());
        assertEquals("Test Poll 2", result.getContent().get(1).getTitle());
    }
} 