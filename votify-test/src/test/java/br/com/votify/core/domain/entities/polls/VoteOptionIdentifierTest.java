package br.com.votify.core.domain.entities.polls;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VoteOptionIdentifierTest {
    @Test
    public void equalsWhenSameObject() {
        VoteOptionIdentifier voteOptionIdentifier = new VoteOptionIdentifier(1L, 1);
        assertEquals(voteOptionIdentifier, voteOptionIdentifier);
    }

    @Test
    public void equalsWhenDifferentObjectsWithSameValue() {
        VoteOptionIdentifier voteOptionIdentifier = new VoteOptionIdentifier(1L, 1);
        VoteOptionIdentifier voteOptionIdentifier2 = new VoteOptionIdentifier(1L, 1);

        assertEquals(voteOptionIdentifier, voteOptionIdentifier2);
    }

    @Test
    public void notEqualsWhenOnlyPollIdEquals() {
        VoteOptionIdentifier voteOptionIdentifier = new VoteOptionIdentifier(1L, 1);
        VoteOptionIdentifier voteOptionIdentifier2 = new VoteOptionIdentifier(1L, 2);

        assertNotEquals(voteOptionIdentifier, voteOptionIdentifier2);
    }

    @Test
    public void notEqualsWhenOnlySequenceEquals() {
        VoteOptionIdentifier voteOptionIdentifier = new VoteOptionIdentifier(1L, 1);
        VoteOptionIdentifier voteOptionIdentifier2 = new VoteOptionIdentifier(2L, 1);

        assertNotEquals(voteOptionIdentifier, voteOptionIdentifier2);
    }

    @Test
    public void notEqualsWhenObjectIsNull() {
        VoteOptionIdentifier voteOptionIdentifier = new VoteOptionIdentifier(1L, 1);
        assertFalse(voteOptionIdentifier.equals(null));
    }

    @Test
    public void notEqualsWhenObjectIsDifferentClass() {
        VoteOptionIdentifier voteOptionIdentifier = new VoteOptionIdentifier(1L, 1);
        assertFalse(voteOptionIdentifier.equals(1));
    }

    @Test
    public void hashWithEqualObjects() {
        VoteOptionIdentifier voteOptionIdentifier = new VoteOptionIdentifier(1L, 1);
        VoteOptionIdentifier voteOptionIdentifier2 = new VoteOptionIdentifier(1L, 1);

        assertEquals(voteOptionIdentifier.hashCode(), voteOptionIdentifier2.hashCode());
    }
}
