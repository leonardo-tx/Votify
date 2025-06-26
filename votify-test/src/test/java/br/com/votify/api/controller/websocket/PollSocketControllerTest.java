package br.com.votify.api.controller.websocket;

import br.com.votify.api.controller.websocket.polls.PollSocketController;
import br.com.votify.core.model.poll.Poll;
import br.com.votify.core.model.poll.VoteOption;
import br.com.votify.core.model.poll.event.PollUpdateEvent;
import br.com.votify.core.model.poll.field.Description;
import br.com.votify.core.model.poll.field.Title;
import br.com.votify.core.model.poll.field.VoteOptionName;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.poll.PollDetailedViewDTO;
import br.com.votify.test.suites.SocketControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PollSocketControllerTest extends SocketControllerTest {
    @Autowired
    private PollSocketController pollSocketController;

    @Test
    void testHandlePollUpdateEvent() throws VotifyException {
        Poll poll = Poll.parseUnsafe(
                1L,
                new Title("Title"),
                new Description(""),
                Instant.now(),
                Instant.now(),
                false,
                List.of(VoteOption.parseUnsafe(new VoteOptionName("Option"), 1, 0, 1L)),
                1,
                2L
        );
        PollUpdateEvent event = new PollUpdateEvent(this, poll);

        pollSocketController.handlePollUpdateEvent(event);

        verify(simpMessagingTemplate, times(1))
                .convertAndSend(eq("/receiver/polls/1"), any(PollDetailedViewDTO.class));
    }
}
