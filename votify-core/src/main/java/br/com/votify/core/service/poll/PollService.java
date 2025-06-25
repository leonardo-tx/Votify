package br.com.votify.core.service.poll;

import br.com.votify.core.model.poll.*;
import br.com.votify.core.model.poll.event.PollUpdateEvent;
import br.com.votify.core.model.user.User;
import br.com.votify.core.repository.poll.VoteRepository;
import br.com.votify.core.repository.poll.PollRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PollService {
    public static final int PAGE_SIZE_LIMIT = 10;

    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public Poll createPoll(PollRegister pollRegister, User responsible) throws VotifyException {
        Poll poll = new Poll(pollRegister, responsible);
        return pollRepository.save(poll);
    }

    public Poll editPoll(Poll poll, User user, Long id) throws VotifyException {
        Optional<Poll> optionalPoll = pollRepository.findById(id);

        if (optionalPoll.isEmpty()) {
            throw new VotifyException(VotifyErrorCode.POLL_NOT_FOUND);
        }

        Poll obj = optionalPoll.get();

        if (!obj.getResponsibleId().equals(user.getId())) {
            throw new VotifyException(VotifyErrorCode.POLL_NOT_OWNED);
        }

        Instant now = Instant.now();

        if (obj.getStartDate().isBefore(now)) {
            if (poll.getEndDate().isBefore(now)) {
                throw new VotifyException(VotifyErrorCode.POLL_END_DATE_INVALID);
            }

            if (poll.getEndDate().equals(obj.getEndDate())) {
                throw new VotifyException(VotifyErrorCode.POLL_ALREADY_IN_PROGRESS);
            }
            obj = Poll.parseUnsafe(
                    obj.getId(),
                    obj.getTitle(),
                    obj.getDescription(),
                    obj.getStartDate(),
                    poll.getEndDate(),
                    obj.isUserRegistration(),
                    obj.getVoteOptions(),
                    obj.getChoiceLimitPerUser(),
                    obj.getResponsibleId()
            );
        } else {
            obj = Poll.parseUnsafe(
                    obj.getId(),
                    poll.getTitle(),
                    poll.getDescription(),
                    poll.getStartDate(),
                    poll.getEndDate(),
                    obj.isUserRegistration(),
                    obj.getVoteOptions(),
                    poll.getChoiceLimitPerUser(),
                    obj.getResponsibleId()
            );
        }

        return pollRepository.save(obj);
    }

    @Transactional
    public Vote vote(Poll poll, VoteRegister voteRegister) throws VotifyException {
        Vote vote = poll.vote(voteRegister);
        if (voteRepository.exists(vote)) {
            throw new VotifyException(VotifyErrorCode.POLL_VOTED_ALREADY);
        }
        pollRepository.save(poll);
        Vote createdVote = voteRepository.save(vote);

        applicationEventPublisher.publishEvent(new PollUpdateEvent(this, poll));
        return createdVote;
    }

    public Vote getVote(Poll poll, User user) {
        Optional<Vote> vote = voteRepository.findByPollAndUser(poll, user);
        return vote.orElseGet(() -> Vote.parseUnsafe(0, poll.getId(), user.getId()));
    }

    public Page<Poll> findAllByUser(User user, int page, int size) throws VotifyException {
        validatePageAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size);
        return pollRepository.findAllByResponsible(user, pageable);
    }

    public Page<Poll> findAllActivePolls(int page, int size) throws VotifyException {
        validatePageAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size);
        return pollRepository.findAllByActives(Instant.now(), pageable);
    }

    public Poll getByIdOrThrow(Long id) throws VotifyException {
        Optional<Poll> optionalPoll = pollRepository.findById(id);
        if (optionalPoll.isEmpty()) {
            throw new VotifyException(VotifyErrorCode.POLL_NOT_FOUND);
        }
        return optionalPoll.get();
    }

    public Page<Poll> findByTitle(String titleSearch, int page, int size) throws VotifyException {
        validatePageAndSize(page, size);
        if (titleSearch == null || titleSearch.isBlank()) {
            throw new VotifyException(VotifyErrorCode.POLL_TITLE_SEARCH_EMPTY);
        }
        Pageable pageable = PageRequest.of(page, size);
        return pollRepository.findByTitleContainingIgnoreCase(titleSearch, Instant.now(), pageable);
    }

    public void cancelPoll(Poll poll, User user) throws VotifyException {
        if (!poll.getResponsibleId().equals(user.getId())) {
            throw new VotifyException(VotifyErrorCode.POLL_NOT_OWNER);
        }

        boolean hasNotStarted = poll.hasNotStarted();
        poll.cancel();

        if (hasNotStarted) {
            pollRepository.delete(poll);
        } else {
            pollRepository.save(poll);
        }
    }

    @Transactional
    public void deletePollInfoFromUser(User user) {
        deleteUserInvalidVotes(user);
        deleteUserNotEndedPolls(user);
    }

    private void validatePageAndSize(int page, int size) throws VotifyException {
        if (page < 0) {
            throw new VotifyException(VotifyErrorCode.POLL_PAGE_INVALID_PAGE);
        }
        if (size < 1 || size > PAGE_SIZE_LIMIT) {
            throw new VotifyException(VotifyErrorCode.POLL_PAGE_LENGTH_INVALID, 1, PAGE_SIZE_LIMIT);
        }
    }

    private void deleteUserInvalidVotes(User user) {
        List<Vote> votes = voteRepository.findAllFromUser(user);
        for (Vote vote : votes) {
            Poll poll = pollRepository.findById(vote.getPollId()).orElseThrow();
            if (poll.hasEnded()) continue;

            poll.removeVote(vote);
            pollRepository.save(poll);
        }
        if (!votes.isEmpty()) {
            voteRepository.deleteAllByUser(user);
        }
    }

    private void deleteUserNotEndedPolls(User user) {
        List<Poll> polls = pollRepository.findAllByResponsible(user);
        for (Poll poll : polls) {
            if (poll.hasEnded()) {
                poll.removeResponsible();
                pollRepository.save(poll);
            } else {
                pollRepository.delete(poll);
            }
        }
    }
}
