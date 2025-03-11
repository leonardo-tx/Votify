package br.com.votify.api.dto.poll;

import br.com.votify.core.domain.entities.poll.Poll;
import br.com.votify.dto.DTOInput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public final class PollInsertDTO implements DTOInput<Poll> {
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean userRegistration;
    private Integer choiceLimitPerUser;

    @Override
    public Poll convertToEntity() {
        return new Poll(title, description, startDate, endDate, userRegistration, choiceLimitPerUser);
    }
}