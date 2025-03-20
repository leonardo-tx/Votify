package br.com.votify.api.controller.poll;

import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.PageResponse;
import br.com.votify.dto.polls.PollDetailedViewDTO;
import br.com.votify.dto.polls.PollInsertDTO;
import br.com.votify.dto.polls.PollListViewDTO;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.domain.entities.poll.Poll;
import br.com.votify.core.service.ContextService;
import br.com.votify.core.service.PollService;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.polls.PollQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        return ApiResponse.success(pollDto, HttpStatus.CREATED).createResponseEntity();
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<PollListViewDTO>>> getUserPolls(
        @PathVariable("userId") Long userId,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size
    ) throws VotifyException {
        Page<Poll> pollPage = pollService.findAllByUserId(userId, page, size);
        List<PollListViewDTO> pollDtos = pollPage.getContent().stream()
                .map(PollListViewDTO::parse)
                .collect(Collectors.toList());
        
        PageResponse<PollListViewDTO> pageResponse = PageResponse.from(pollPage, pollDtos);
        return ApiResponse.success(pageResponse, HttpStatus.OK).createResponseEntity();
    }
    
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<PageResponse<PollListViewDTO>>> getMyPolls(
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size
    ) throws VotifyException {
        
        Optional<User> userOptional = contextService.getUserOptional();
        Long userId = userOptional.map(User::getId).orElse(null);
        
        if (userId == null) {
            PageResponse<PollListViewDTO> pageResponse = new PageResponse<>(List.of(), 0, size, 0, 0, true, true);
            return ApiResponse.success(pageResponse, HttpStatus.OK).createResponseEntity();
        }

        Page<Poll> pollPage = pollService.findAllByUserId(userId, page, size);
        List<PollListViewDTO> pollDtos = pollPage.getContent().stream()
                .map(PollListViewDTO::parse)
                .collect(Collectors.toList());
        
        PageResponse<PollListViewDTO> pageResponse = PageResponse.from(pollPage, pollDtos);
        return ApiResponse.success(pageResponse, HttpStatus.OK).createResponseEntity();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PollQueryDto>> getPollById(
        @PathVariable Long id
    ) {
        Optional<User> user = contextService.getUserOptional();
        Poll poll = pollService.findSpecificPoll(id);

        if (user.isEmpty()) {
            PollQueryDto dto = PollQueryDto.parse(poll, List.of());
            return ApiResponse.success(dto, HttpStatus.OK).createResponseEntity();
        }

        PollQueryDto dto = PollQueryDto.parse(poll);
        ApiResponse.success(dto, HttpStatus.OK).createResponseEntity();
    }
}
