package br.com.votify.core.service;

import br.com.votify.core.domain.entities.poll.Poll;
import br.com.votify.core.repository.PollRepository;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;

    public Poll createPoll(Poll poll, Long userId) throws VotifyException {

        if (pollRepository.existsByTitleAndResponsibleId(poll.getTitle(),userId)) {
            throw new VotifyException(VotifyErrorCode.POLL_TITLE_ALREADY_EXISTS_FOR_THIS_USER);
        }

        return pollRepository.save(poll);
    }
}
