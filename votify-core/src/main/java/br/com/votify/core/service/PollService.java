package br.com.votify.core.service;

import br.com.votify.core.domain.entities.polls.*;
import br.com.votify.core.repository.VoteRepository;
import br.com.votify.core.utils.validators.PollValidator;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.PollRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.core.utils.validators.VoteValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PollService {
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;

    public Poll createPoll(Poll poll, User responsible) throws VotifyException {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        if (poll.getStartDate() == null) {
            poll.setStartDate(now);
        }
        PollValidator.validateFields(poll, now);
        poll.setResponsible(responsible);
        poll.setId(null);
        poll.setVotes(null);

        for (int i = 0; i < poll.getVoteOptions().size(); i++) {
            VoteOption voteOption = poll.getVoteOptions().get(i);
            voteOption.setId(new VoteOptionIdentifier(null, i));
            voteOption.setCount(0);
        }
        return pollRepository.save(poll);
    }

    @Transactional
    public Vote vote(Vote vote, Poll poll, User user) throws VotifyException {
        vote.setId(new VoteIdentifier(poll.getId(), user.getId()));
        vote.setUser(user);
        vote.setPoll(poll);

        VoteValidator.validateFields(vote);
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        if (now.isBefore(vote.getPoll().getStartDate()) || now.isAfter(vote.getPoll().getEndDate())) {
            throw new VotifyException(VotifyErrorCode.POLL_VOTE_OUT_OF_DATE_INTERVAL);
        }
        if (voteRepository.existsById(vote.getId())) {
            throw new VotifyException(VotifyErrorCode.POLL_VOTED_ALREADY);
        }
        for (VoteOption voteOption : poll.getVoteOptions()) {
            if (voteOption.hasBeenVoted(vote)) {
                voteOption.incrementCount();
            }
        }
        return voteRepository.save(vote);
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

    public Poll getByIdOrThrow(Long id) throws VotifyException {
        Optional<Poll> optionalPoll = pollRepository.findById(id);
        if (optionalPoll.isEmpty()) {
            throw new VotifyException(VotifyErrorCode.POLL_NOT_FOUND);
        }
        return optionalPoll.get();
    }

    public Page<Poll> findByTitle(String title, int page, int size) throws VotifyException {
        if (page < 0) {
            throw new VotifyException(VotifyErrorCode.POLL_PAGE_INVALID_PAGE);
        }
        if (size < 1 || size > Poll.PAGE_SIZE_LIMIT) {
            throw new VotifyException(VotifyErrorCode.POLL_PAGE_LENGTH_INVALID, 1, Poll.PAGE_SIZE_LIMIT);
        }
        
        if (title == null || title.trim().isEmpty() || title.trim().equals(" ") || title.isBlank()) {
            throw new VotifyException(VotifyErrorCode.POLL_TITLE_SEARCH_EMPTY);
        }
        
        Pageable pageable = PageRequest.of(page, size);
        return pollRepository.findByTitleContainingIgnoreCase(title, pageable);
    }
}
