package br.com.votify.api.controller.websocket.polls;

import br.com.votify.core.domain.events.PollUpdateEvent;
import br.com.votify.core.service.PollService;
import br.com.votify.dto.polls.PollDetailedViewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class PollSocketController {
    private final PollService pollService;
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
