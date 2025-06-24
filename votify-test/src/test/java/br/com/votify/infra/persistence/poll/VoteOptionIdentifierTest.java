package br.com.votify.infra.persistence.poll;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VoteOptionIdentifierTest {
    @Test
    void equalsWhenSameObject() {
        VoteOptionIdentifier voteOptionIdentifier = new VoteOptionIdentifier(1L, 1);
        assertEquals(voteOptionIdentifier, voteOptionIdentifier);
    }

    @Test
    void equalsWhenDifferentObjectsWithSameValue() {
        VoteOptionIdentifier voteOptionIdentifier = new VoteOptionIdentifier(1L, 1);
        VoteOptionIdentifier voteOptionIdentifier2 = new VoteOptionIdentifier(1L, 1);

        assertEquals(voteOptionIdentifier, voteOptionIdentifier2);
    }

    @Test
    void notEqualsWhenOnlyPollIdEquals() {
        VoteOptionIdentifier voteOptionIdentifier = new VoteOptionIdentifier(1L, 1);
        VoteOptionIdentifier voteOptionIdentifier2 = new VoteOptionIdentifier(1L, 2);

        assertNotEquals(voteOptionIdentifier, voteOptionIdentifier2);
    }

    @Test
    void notEqualsWhenOnlySequenceEquals() {
        VoteOptionIdentifier voteOptionIdentifier = new VoteOptionIdentifier(1L, 1);
        VoteOptionIdentifier voteOptionIdentifier2 = new VoteOptionIdentifier(2L, 1);

        assertNotEquals(voteOptionIdentifier, voteOptionIdentifier2);
    }

    @Test
    void notEqualsWhenObjectIsNull() {
        VoteOptionIdentifier voteOptionIdentifier = new VoteOptionIdentifier(1L, 1);
        assertFalse(voteOptionIdentifier.equals(null));
    }

    @Test
    void notEqualsWhenObjectIsDifferentClass() {
        VoteOptionIdentifier voteOptionIdentifier = new VoteOptionIdentifier(1L, 1);
        assertFalse(voteOptionIdentifier.equals(1));
    }

    @Test
    void hashWithEqualObjects() {
        VoteOptionIdentifier voteOptionIdentifier = new VoteOptionIdentifier(1L, 1);
        VoteOptionIdentifier voteOptionIdentifier2 = new VoteOptionIdentifier(1L, 1);

        assertEquals(voteOptionIdentifier.hashCode(), voteOptionIdentifier2.hashCode());
    }
}
