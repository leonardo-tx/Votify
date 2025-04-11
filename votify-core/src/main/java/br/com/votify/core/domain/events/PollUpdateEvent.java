package br.com.votify.core.domain.events;

import br.com.votify.core.domain.entities.polls.Poll;
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
