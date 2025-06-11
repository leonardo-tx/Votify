package br.com.votify.infra.persistence.poll;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteOptionIdentifier implements Serializable {
    private Long pollId;
    private Integer sequence;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VoteOptionIdentifier that = (VoteOptionIdentifier) o;
        return Objects.equals(pollId, that.pollId) && Objects.equals(sequence, that.sequence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pollId, sequence);
    }
}
