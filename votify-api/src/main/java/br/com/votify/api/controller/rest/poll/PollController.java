package br.com.votify.api.controller.rest.poll;

import br.com.votify.core.model.poll.PollRegister;
import br.com.votify.core.model.poll.VoteRegister;
import br.com.votify.core.model.user.User;
import br.com.votify.core.service.user.UserService;
import br.com.votify.core.service.user.decorators.NeedsUserContext;
import br.com.votify.core.model.poll.Vote;
import br.com.votify.dto.ApiResponse;
import br.com.votify.dto.PageResponse;
import br.com.votify.dto.poll.*;
import br.com.votify.core.model.poll.Poll;
import br.com.votify.core.service.poll.PollService;
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
    private final UserService userService;

    @PostMapping("{id}/vote")
    @NeedsUserContext
    public ResponseEntity<ApiResponse<Integer>> voteAtPoll(
            @PathVariable("id") Long id,
            @RequestBody VoteInsertDTO voteInsertDTO
    ) throws VotifyException {
        User user = userService.getContext().getUserOrThrow();
        Poll poll = pollService.getByIdOrThrow(id);
        VoteRegister voteRegister = voteInsertDTO.convertToEntity(user, poll);

        Vote createdVote = pollService.vote(poll, voteRegister);
        return ApiResponse.success(createdVote.getOption(), HttpStatus.CREATED).createResponseEntity();
    }

    @PutMapping("/{id}")
    @NeedsUserContext
    public ResponseEntity<ApiResponse<PollDetailedViewDTO>> editPoll(
            @RequestBody PollInsertDTO pollInsertDTO,
            @PathVariable("id") Long id) throws VotifyException {

        User user = userService.getContext().getUserOrThrow();
        PollRegister pollRegister = pollInsertDTO.convertToEntity();

        Poll pollToUpdate = new Poll(pollRegister, user);

        Poll updatedPoll = pollService.editPoll(pollToUpdate, user, id);
        PollDetailedViewDTO pollDto = PollDetailedViewDTO.parse(updatedPoll, 0);

        return ApiResponse.success(pollDto, HttpStatus.OK).createResponseEntity();
    }

    @PostMapping
    @NeedsUserContext
    public ResponseEntity<ApiResponse<PollDetailedViewDTO>> insertPoll(
            @RequestBody PollInsertDTO pollInsertDTO
    ) throws VotifyException {
        User user = userService.getContext().getUserOrThrow();
        Poll poll = pollService.createPoll(pollInsertDTO.convertToEntity(), user);
        PollDetailedViewDTO pollDto = PollDetailedViewDTO.parse(poll, 0);

        return ApiResponse.success(pollDto, HttpStatus.CREATED).createResponseEntity();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<PollListViewDTO>>> getUserPolls(
            @PathVariable("userId") Long userId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) throws VotifyException {
        User user = userService.getUserById(userId);
        Page<Poll> pollPage = pollService.findAllByUser(user, page, size);
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
        Optional<User> userOptional = userService.getContext().getUserOptional();
        if (userOptional.isEmpty()) {
            PageResponse<PollListViewDTO> pageResponse = new PageResponse<>(List.of(), 0, size, 0, 0, true, true);
            return ApiResponse.success(pageResponse, HttpStatus.OK).createResponseEntity();
        }

        Page<Poll> pollPage = pollService.findAllByUser(userOptional.get(), page, size);
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
        Optional<User> userOptional = userService.getContext().getUserOptional();
        Poll poll = pollService.getByIdOrThrow(id);
        if (userOptional.isPresent()) {
            Vote vote = pollService.getVote(poll, userOptional.get());
            PollDetailedViewDTO dto = PollDetailedViewDTO.parse(poll, vote.getOption());
            return ApiResponse.success(dto, HttpStatus.OK).createResponseEntity();
        }
        PollDetailedViewDTO dto = PollDetailedViewDTO.parse(poll, 0);
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
        User user = userService.getContext().getUserOrThrow();
        Poll poll = pollService.getByIdOrThrow(id);
        pollService.cancelPoll(poll, user);

        return ApiResponse.success(null, HttpStatus.OK).createResponseEntity();
    }
}
