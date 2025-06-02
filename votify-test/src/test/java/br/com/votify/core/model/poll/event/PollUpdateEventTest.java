package br.com.votify.core.model.poll.event;

import br.com.votify.core.model.poll.Poll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class PollUpdateEventTest {
    @Test
    void testPollUpdateEventValidConstruct() {
        Poll poll = mock(Poll.class);
        PollUpdateEvent pollUpdateEvent = new PollUpdateEvent(this, poll);

        assertEquals(poll, pollUpdateEvent.getPoll());
        assertEquals(this, pollUpdateEvent.getSource());
        assertTrue(pollUpdateEvent.getTimestamp() <= Instant.now().toEpochMilli());
    }
}
