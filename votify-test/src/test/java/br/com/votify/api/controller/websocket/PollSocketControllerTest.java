package br.com.votify.api.controller.websocket;

import br.com.votify.api.controller.websocket.polls.PollSocketController;
import br.com.votify.core.model.poll.Poll;
import br.com.votify.core.model.poll.event.PollUpdateEvent;
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
        Poll poll = Poll.parseUnsafe(1L, null, null, null, null, false, List.of(), 1, 1L);
        PollUpdateEvent event = new PollUpdateEvent(this, poll);

        pollSocketController.handlePollUpdateEvent(event);

        verify(simpMessagingTemplate, times(1))
                .convertAndSend(eq("/receiver/polls/1"), any(PollDetailedViewDTO.class));
    }
}
