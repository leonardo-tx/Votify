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

    public Poll editPoll(Poll poll, User user, Long id) {
        return pollRepository.findById(id)
                .map(obj -> {
                    LocalDateTime now = LocalDateTime.now();
                    if(obj.getStartDate().isBefore(now)) {
                        if (poll.getEndDate().isBefore(now)) {
                            throw new RuntimeException("A nova data de término deve ser no futuro.");
                        }

                        if (poll.getEndDate().isEqual(obj.getEndDate())) {
                            throw new RuntimeException("Não é possível alterar enquete, já esta em andamento.");
                        }
                        obj.setEndDate(poll.getEndDate());
                    } else {
                        obj.setTitle(poll.getTitle());
                        obj.setDescription(poll.getDescription());
                        obj.setStartDate(poll.getStartDate());
                        obj.setEndDate(poll.getEndDate());
                        obj.setChoiceLimitPerUser(poll.getChoiceLimitPerUser());
                        obj.setResponsible(user);
                    }
                    return pollRepository.save(obj);
                })
                .orElseThrow(() -> new RuntimeException("Enquete não foi localizada."));
    }
}
