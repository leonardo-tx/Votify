package br.com.votify.core.model.poll.event;

import br.com.votify.core.model.poll.Poll;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PollUpdateEvent extends ApplicationEvent {
    private final Poll poll;

    public PollUpdateEvent(Object source, Poll poll) {
        super(source);
        this.poll = poll;
    }
}
