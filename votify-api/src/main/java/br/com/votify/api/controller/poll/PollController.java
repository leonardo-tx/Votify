package br.com.votify.api.controller.poll;

import br.com.votify.dto.poll.PollDetailedViewDTO;
import br.com.votify.dto.poll.PollInsertDTO;
import br.com.votify.core.domain.entities.poll.Poll;
import br.com.votify.core.service.ContextService;
import br.com.votify.core.service.PollService;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/polls")
public class PollController {

    private final PollService pollService;
    private final ContextService contextService;


    @PostMapping
    public ResponseEntity<ApiResponse<PollDetailedViewDTO>> insertPoll(@RequestBody PollInsertDTO pollInsertDTO) throws VotifyException {
            contextService.throwIfNotAuthenticated();
            Poll poll = pollService.createPoll(pollInsertDTO.convertToEntity(), contextService.getUserOrThrow());
            PollDetailedViewDTO pollDto = PollDetailedViewDTO.parse(poll);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(pollDto));
    }
}
