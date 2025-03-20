package br.com.votify.core.pollTemp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PollService {

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private VoteRepository voteRepository;

    /**
     *
     * @param pollId  ID da poll a buscar
     * @param userId  ID do usuário logado (pode ser nulo se não estiver logado)
     * @return PollResponseDto
     */
    public PollResponseDto findSpecificPoll(Long pollId, Long userId) {
        Optional<Poll> opPoll = pollRepository.findById(pollId);

        if (!opPoll.isPresent()) {
            throw new RuntimeException("Poll not found");
        }

        Poll poll = opPoll.get();
        String userVote = "no vote";

        if (userId != null) {
            Optional<Vote> opVote = voteRepository.findByPollIdAndUserId(pollId, userId);
            if (opVote.isPresent()) {
                userVote = opVote.get().getChosenOption();
            }
        }

        return PollResponseDto.builder()
                .id(poll.getId())
                .question(poll.getQuestion())
                .description(poll.getDescription())
                .userVote(userVote)
                .build();
    }
}