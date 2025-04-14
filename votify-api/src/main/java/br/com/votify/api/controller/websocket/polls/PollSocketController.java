package br.com.votify.api.controller.websocket.polls;

import br.com.votify.core.domain.entities.polls.Poll;
import br.com.votify.core.service.PollService;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.polls.PollDetailedViewDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class PollSocketController {
    private final PollService pollService;

    @SendTo("/receiver/polls/{id}")
    public PollDetailedViewDTO getPollUpdates(@DestinationVariable Long id) throws VotifyException {
        Poll poll = pollService.getByIdOrThrow(id);
        return PollDetailedViewDTO.parse(poll, 0);
    }
}
