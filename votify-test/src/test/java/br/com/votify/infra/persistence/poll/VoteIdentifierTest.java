package br.com.votify.infra.persistence.poll;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VoteIdentifierTest {
    @Test
    void equalsWhenSameObject() {
        VoteIdentifier voteIdentifier = new VoteIdentifier(1L, 1L);
        assertEquals(voteIdentifier, voteIdentifier);
    }

    @Test
    void equalsWhenDifferentObjectsWithSameValue() {
        VoteIdentifier voteIdentifier = new VoteIdentifier(1L, 1L);
        VoteIdentifier voteIdentifier2 = new VoteIdentifier(1L, 1L);

        assertEquals(voteIdentifier, voteIdentifier2);
    }

    @Test
    void notEqualsWhenOnlyPollIdEquals() {
        VoteIdentifier voteIdentifier = new VoteIdentifier(1L, 1L);
        VoteIdentifier voteIdentifier2 = new VoteIdentifier(1L, 2L);

        assertNotEquals(voteIdentifier, voteIdentifier2);
    }

    @Test
    void notEqualsWhenOnlyUserIdEquals() {
        VoteIdentifier voteIdentifier = new VoteIdentifier(1L, 1L);
        VoteIdentifier voteIdentifier2 = new VoteIdentifier(2L, 1L);

        assertNotEquals(voteIdentifier, voteIdentifier2);
    }

    @Test
    void notEqualsWhenObjectIsNull() {
        VoteIdentifier voteIdentifier = new VoteIdentifier(1L, 1L);
        assertFalse(voteIdentifier.equals(null));
    }

    @Test
    void notEqualsWhenObjectIsDifferentClass() {
        VoteIdentifier voteIdentifier = new VoteIdentifier(1L, 1L);
        assertFalse(voteIdentifier.equals(1));
    }

    @Test
    void hashWithEqualObjects() {
        VoteIdentifier voteIdentifier = new VoteIdentifier(1L, 1L);
        VoteIdentifier voteIdentifier2 = new VoteIdentifier(1L, 1L);

        assertEquals(voteIdentifier.hashCode(), voteIdentifier2.hashCode());
    }
}
