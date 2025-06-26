package br.com.votify.infra.persistence.poll;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoteIdentifier implements Serializable {
    private Long pollId;
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VoteIdentifier voteIdentifier = (VoteIdentifier) o;
        return Objects.equals(userId, voteIdentifier.userId) && Objects.equals(pollId, voteIdentifier.pollId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, pollId);
    }
}
