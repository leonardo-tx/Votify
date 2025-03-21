package br.com.votify.core.service;

import br.com.votify.core.domain.entities.polls.Poll;
import br.com.votify.core.utils.validators.PollValidator;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.PollRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class PollService {
    private final PollRepository pollRepository;

    public Poll createPoll(Poll poll, User user) throws VotifyException {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        if (poll.getStartDate() == null) {
            poll.setStartDate(now);
        }
        PollValidator.validateFields(poll, now);
        poll.setResponsible(user);

        if (pollRepository.existsByTitleAndResponsibleId(poll.getTitle(), user.getId())) {
            throw new VotifyException(VotifyErrorCode.POLL_TITLE_ALREADY_EXISTS_FOR_THIS_USER);
        }

        return pollRepository.save(poll);
    }
    
    public Page<Poll> findAllByUserId(Long userId, int page, int size) throws VotifyException {
        if (page < 0) {
            throw new VotifyException(VotifyErrorCode.POLL_PAGE_INVALID_PAGE);
        }
        if (size < 1 || size > Poll.PAGE_SIZE_LIMIT) {
            throw new VotifyException(VotifyErrorCode.POLL_PAGE_LENGTH_INVALID, 1, Poll.PAGE_SIZE_LIMIT);
        }
        Pageable pageable = PageRequest.of(page, size);
        return pollRepository.findAllByResponsibleId(userId, pageable);
    }
}
