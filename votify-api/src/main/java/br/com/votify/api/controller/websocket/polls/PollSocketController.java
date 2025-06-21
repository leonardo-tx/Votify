package br.com.votify.api.controller.websocket.polls;

import br.com.votify.core.model.poll.event.PollUpdateEvent;
import br.com.votify.dto.poll.PollDetailedViewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class PollSocketController {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @EventListener
    public void handlePollUpdateEvent(PollUpdateEvent event) {
        PollDetailedViewDTO response = PollDetailedViewDTO.parse(event.getPoll(), 0);
        simpMessagingTemplate.convertAndSend(
                "/receiver/polls/" + event.getPoll().getId(),
                response
        );
    }
}
