package br.com.votify.core.domain.entities.polls;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VoteIdentifierTest {
    @Test
    public void equalsWhenSameObject() {
        VoteIdentifier voteIdentifier = new VoteIdentifier(1L, 1L);
        assertEquals(voteIdentifier, voteIdentifier);
    }

    @Test
    public void equalsWhenDifferentObjectsWithSameValue() {
        VoteIdentifier voteIdentifier = new VoteIdentifier(1L, 1L);
        VoteIdentifier voteIdentifier2 = new VoteIdentifier(1L, 1L);

        assertEquals(voteIdentifier, voteIdentifier2);
    }

    @Test
    public void notEqualsWhenOnlyPollIdEquals() {
        VoteIdentifier voteIdentifier = new VoteIdentifier(1L, 1L);
        VoteIdentifier voteIdentifier2 = new VoteIdentifier(1L, 2L);

        assertNotEquals(voteIdentifier, voteIdentifier2);
    }

    @Test
    public void notEqualsWhenOnlyUserIdEquals() {
        VoteIdentifier voteIdentifier = new VoteIdentifier(1L, 1L);
        VoteIdentifier voteIdentifier2 = new VoteIdentifier(2L, 1L);

        assertNotEquals(voteIdentifier, voteIdentifier2);
    }

    @Test
    public void notEqualsWhenObjectIsNull() {
        VoteIdentifier voteIdentifier = new VoteIdentifier(1L, 1L);
        assertFalse(voteIdentifier.equals(null));
    }

    @Test
    public void notEqualsWhenObjectIsDifferentClass() {
        VoteIdentifier voteIdentifier = new VoteIdentifier(1L, 1L);
        assertFalse(voteIdentifier.equals(1));
    }

    @Test
    public void hashWithEqualObjects() {
        VoteIdentifier voteIdentifier = new VoteIdentifier(1L, 1L);
        VoteIdentifier voteIdentifier2 = new VoteIdentifier(1L, 1L);

        assertEquals(voteIdentifier.hashCode(), voteIdentifier2.hashCode());
    }
}
