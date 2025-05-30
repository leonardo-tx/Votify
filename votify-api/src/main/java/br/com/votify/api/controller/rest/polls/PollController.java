package br.com.votify.api.controller.rest.polls;

import br.com.votify.core.decorators.NeedsUserContext;
import br.com.votify.core.domain.entities.polls.Vote;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.PageResponse;
import br.com.votify.dto.polls.*;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.domain.entities.polls.Poll;
import br.com.votify.core.service.ContextService;
import br.com.votify.core.service.PollService;
import br.com.votify.core.utils.exceptions.VotifyException;
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

    @PostMapping("{id}/vote")
    @NeedsUserContext
    public ResponseEntity<ApiResponse<Integer>> voteAtPoll(
            @PathVariable("id") Long id,
            @RequestBody VoteInsertDTO voteInsertDTO
    ) throws VotifyException {
        User user = contextService.getUserOrThrow();
        Poll poll = pollService.getByIdOrThrow(id);
        Vote vote = voteInsertDTO.convertToEntity();

        Vote createdVote = pollService.vote(vote, poll, user);
        return ApiResponse.success(createdVote.getOption(), HttpStatus.CREATED).createResponseEntity();
    }

    @PostMapping
    @NeedsUserContext
    public ResponseEntity<ApiResponse<PollDetailedViewDTO>> insertPoll(
            @RequestBody PollInsertDTO pollInsertDTO
    ) throws VotifyException {
        Poll poll = pollService.createPoll(pollInsertDTO.convertToEntity(), contextService.getUserOrThrow());
        PollDetailedViewDTO pollDto = PollDetailedViewDTO.parse(poll, 0);

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

    @GetMapping("/me")
    @NeedsUserContext
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

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<PollListViewDTO>>> findByTitle(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) throws VotifyException {
        Page<Poll> pollPage = pollService.findByTitle(title, page, size);
        List<PollListViewDTO> pollDtos = pollPage.getContent().stream()
                .map(PollListViewDTO::parse)
                .collect(Collectors.toList());

        PageResponse<PollListViewDTO> pageResponse = PageResponse.from(pollPage, pollDtos);
        return ApiResponse.success(pageResponse, HttpStatus.OK).createResponseEntity();
    }

    @GetMapping("/{id}")
    @NeedsUserContext
    public ResponseEntity<ApiResponse<PollDetailedViewDTO>> getPollById(
        @PathVariable("id") Long id
    ) throws VotifyException {
        Optional<User> userOptional = contextService.getUserOptional();
        Poll poll = pollService.getByIdOrThrow(id);
        if (userOptional.isPresent()) {
            Vote vote = pollService.getVote(poll, userOptional.get());
            PollDetailedViewDTO dto = PollDetailedViewDTO.parse(poll, vote.getOption());
            return ApiResponse.success(dto, HttpStatus.OK).createResponseEntity();
        }
        Vote vote = new Vote();
        PollDetailedViewDTO dto = PollDetailedViewDTO.parse(poll, vote.getOption());
        return ApiResponse.success(dto, HttpStatus.OK).createResponseEntity();
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<PageResponse<PollListViewDTO>>> getActivePolls(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) throws VotifyException {
        Page<Poll> pollPage = pollService.findAllActivePolls(page, size);
        List<PollListViewDTO> pollDtos = pollPage.getContent().stream()
                .map(PollListViewDTO::parse)
                .collect(Collectors.toList());

        PageResponse<PollListViewDTO> pageResponse = PageResponse.from(pollPage, pollDtos);
        return ApiResponse.success(pageResponse, HttpStatus.OK).createResponseEntity();
    }

    @DeleteMapping("/{id}/cancel")
    @NeedsUserContext
    public ResponseEntity<ApiResponse<Object>> cancelPoll(@PathVariable("id") Long id) throws VotifyException {
        User user = contextService.getUserOrThrow();
        Poll poll = pollService.getByIdOrThrow(id);
        pollService.cancelPoll(poll, user);

        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }
}
