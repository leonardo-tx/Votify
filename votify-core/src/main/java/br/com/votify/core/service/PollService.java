package br.com.votify.core.service;

import br.com.votify.core.domain.entities.poll.Poll;
import br.com.votify.core.utils.validators.PollValidator;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.repository.PollRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.RequiredArgsConstructor;
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
}
