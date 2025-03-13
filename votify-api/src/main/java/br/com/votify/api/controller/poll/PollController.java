package br.com.votify.api.controller.poll;

import br.com.votify.core.domain.entities.users.User;
import br.com.votify.dto.polls.PollDetailedViewDTO;
import br.com.votify.dto.polls.PollInsertDTO;
import br.com.votify.core.domain.entities.poll.Poll;
import br.com.votify.core.service.ContextService;
import br.com.votify.core.service.PollService;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.polls.PollQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/polls")
public class PollController {
    private final PollService pollService;
    private final ContextService contextService;

    @PostMapping
    public ResponseEntity<ApiResponse<PollDetailedViewDTO>> insertPoll(@RequestBody PollInsertDTO pollInsertDTO) throws VotifyException {
        Poll poll = pollService.createPoll(pollInsertDTO.convertToEntity(), contextService.getUserOrThrow());
        PollDetailedViewDTO pollDto = PollDetailedViewDTO.parse(poll);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(pollDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PollQueryDto>> getPollById(
        @PathVariable Long id
    ) {
        Optional<User> user = contextService.getUserOptional();
        Poll poll = pollService.findSpecificPoll(id);

        if (user.isEmpty()) {
            PollQueryDto dto = PollQueryDto.parse(poll, List.of());
            return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(dto));
        }

        PollQueryDto dto = PollQueryDto.parse(poll);
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(dto));
    }
}
