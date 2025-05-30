package br.com.votify.core.service;

import br.com.votify.core.domain.entities.polls.*;
import br.com.votify.core.domain.events.PollUpdateEvent;
import br.com.votify.core.repository.VoteRepository;
import br.com.votify.core.utils.validators.PollValidator;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.PollRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.core.utils.validators.VoteValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PollService {
    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public Poll createPoll(Poll poll, User responsible) throws VotifyException {
        Instant now = Instant.now();
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
        if (poll.isOutOfDate()) {
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
        pollRepository.save(poll);
        Vote createdVote = voteRepository.save(vote);

        applicationEventPublisher.publishEvent(new PollUpdateEvent(this, poll));
        return createdVote;
    }

    public Vote getVote(Poll poll, User user) {
        VoteIdentifier voteIdentifier = new VoteIdentifier(poll.getId(), user.getId());
        Optional<Vote> vote = voteRepository.findById(voteIdentifier);

        return vote.orElseGet(() -> new Vote(voteIdentifier, 0, poll, user));
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

    public Page<Poll> findAllActivePolls(int page, int size) throws VotifyException {
        if (page < 0) {
            throw new VotifyException(VotifyErrorCode.POLL_PAGE_INVALID_PAGE);
        }
        if (size < 1 || size > Poll.PAGE_SIZE_LIMIT) {
            throw new VotifyException(VotifyErrorCode.POLL_PAGE_LENGTH_INVALID, 1, Poll.PAGE_SIZE_LIMIT);
        }
        Pageable pageable = PageRequest.of(page, size);
        return pollRepository.findAllByActives(Instant.now(), pageable);
    }


    /**
     *
     * @param id  ID da poll a buscar
     * @return Poll
     */
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

        if (title == null || title.isBlank()) {
            throw new VotifyException(VotifyErrorCode.POLL_TITLE_SEARCH_EMPTY);
        }

        Pageable pageable = PageRequest.of(page, size);
        return pollRepository.findByTitleContainingIgnoreCase(title, Instant.now(), pageable);
    }

    public void cancelPoll(Poll poll, User user) throws VotifyException {
        if (!poll.getResponsible().getId().equals(user.getId())) {
            throw new VotifyException(VotifyErrorCode.POLL_NOT_OWNER);
        }
        if (poll.hasEnded()) {
            throw new VotifyException(VotifyErrorCode.POLL_CANNOT_CANCEL_FINISHED);
        }
        if (poll.hasNotStarted()) {
            pollRepository.delete(poll);
            return;
        }
        poll.setEndDate(Instant.now());
        pollRepository.save(poll);
    }
}
