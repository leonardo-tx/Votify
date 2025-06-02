package br.com.votify.api.controller.websocket;

import br.com.votify.api.controller.websocket.polls.PollSocketController;
import br.com.votify.core.domain.entities.polls.Poll;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.events.PollUpdateEvent;
import br.com.votify.dto.polls.PollDetailedViewDTO;
import br.com.votify.test.suites.SocketControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PollSocketControllerTest extends SocketControllerTest {
    @Autowired
    private PollSocketController pollSocketController;

    @MockitoBean
    private SimpMessagingTemplate simpMessagingTemplate;

    @Test
    public void testHandlePollUpdateEvent() {
        User user = CommonUser.builder()
                .id(1L)
                .build();
        Poll poll = Poll.builder()
                .id(1L)
                .voteOptions(List.of())
                .votes(List.of())
                .responsible(user)
                .build();
        PollUpdateEvent event = new PollUpdateEvent(this, poll);

        pollSocketController.handlePollUpdateEvent(event);

        verify(simpMessagingTemplate, times(1))
                .convertAndSend(eq("/receiver/polls/1"), any(PollDetailedViewDTO.class));
    }
}
