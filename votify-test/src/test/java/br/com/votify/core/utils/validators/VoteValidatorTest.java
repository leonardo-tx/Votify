package br.com.votify.core.utils.validators;

import br.com.votify.core.domain.entities.polls.Poll;
import br.com.votify.core.domain.entities.polls.Vote;
import br.com.votify.core.domain.entities.polls.VoteOption;
import br.com.votify.core.domain.entities.users.CommonUser;
import br.com.votify.core.domain.entities.users.User;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import net.jqwik.api.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class VoteValidatorTest {
    @Test
    public void testValidVote() {
        User user = new CommonUser();
        Poll poll = new Poll();
        poll.setChoiceLimitPerUser(2);
        poll.setVoteOptions(List.of(
                new VoteOption(null, "Option 1", 0, poll),
                new VoteOption(null, "Option 2", 0, poll),
                new VoteOption(null, "Option 3", 0, poll),
                new VoteOption(null, "Option 4", 0, poll),
                new VoteOption(null, "Option 5", 0, poll)
        ));

        Vote vote = new Vote(null, 17, poll, user);
        assertDoesNotThrow(() -> VoteValidator.validateFields(vote));
    }

    @Test
    public void testVoteWithNullUser() {
        User user = null;
        Poll poll = new Poll();
        poll.setChoiceLimitPerUser(2);
        poll.setVoteOptions(List.of(
                new VoteOption(null, "Option 1", 0, poll),
                new VoteOption(null, "Option 2", 0, poll),
                new VoteOption(null, "Option 3", 0, poll),
                new VoteOption(null, "Option 4", 0, poll),
                new VoteOption(null, "Option 5", 0, poll)
        ));

        Vote vote = new Vote(null, 17, poll, user);
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> VoteValidator.validateFields(vote)
        );
        assertEquals(VotifyErrorCode.COMMON_UNAUTHORIZED, exception.getErrorCode());
    }

    @Test
    public void testVoteWithNullPoll() {
        User user = new CommonUser();
        Poll poll = null;

        Vote vote = new Vote(null, 17, poll, user);
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> VoteValidator.validateFields(vote)
        );
        assertEquals(VotifyErrorCode.POLL_NOT_FOUND, exception.getErrorCode());
    }

    @Property
    void testValidVoteValues(
            @ForAll("validVoteData") Tuple.Tuple3<List<VoteOption>, Integer, Integer> voteData
    ) {
        assertDoesNotThrow(() -> VoteValidator.validateValue(voteData.get3(), voteData.get1(), voteData.get2()));
    }

    @Property
    void testVoteEqualToZero(
            @ForAll("voteDataWithVoteValueEqualToZero") Tuple.Tuple3<List<VoteOption>, Integer, Integer> voteData
    ) {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> VoteValidator.validateValue(voteData.get3(), voteData.get1(), voteData.get2())
        );
        assertEquals(VotifyErrorCode.POLL_VOTE_EMPTY, exception.getErrorCode());
    }

    @Property
    void testInvalidVoteValues(
            @ForAll("voteDataWithInvalidVoteValue") Tuple.Tuple3<List<VoteOption>, Integer, Integer> voteData
    ) {
        VotifyException exception = assertThrows(
                VotifyException.class,
                () -> VoteValidator.validateValue(voteData.get3(), voteData.get1(), voteData.get2())
        );
        assertEquals(VotifyErrorCode.POLL_VOTE_INVALID, exception.getErrorCode());
    }

    @Provide
    Arbitrary<Tuple.Tuple3<List<VoteOption>, Integer, Integer>> validVoteData() {
        return Arbitraries.integers().between(Poll.VOTE_OPTIONS_MIN, Poll.VOTE_OPTIONS_MAX).flatMap(numOptions -> {
            List<VoteOption> voteOptions = IntStream.range(0, numOptions)
                    .mapToObj(i -> new VoteOption())
                    .toList();

            Arbitrary<Integer> choiceLimitArbitrary = Arbitraries.integers().between(1, numOptions);
            int max = (1 << numOptions) - 1;
            return choiceLimitArbitrary.flatMap(choiceLimit -> {
                Arbitrary<Integer> voteValueArbitrary = Arbitraries.integers()
                        .between(1, max)
                        .filter(value -> Integer.bitCount(value) <= choiceLimit);
                return voteValueArbitrary.map(voteValue -> Tuple.of(voteOptions, choiceLimit, voteValue));
            });
        });
    }

    @Provide
    Arbitrary<Tuple.Tuple3<List<VoteOption>, Integer, Integer>> voteDataWithVoteValueEqualToZero() {
        return Arbitraries.integers().between(Poll.VOTE_OPTIONS_MIN, Poll.VOTE_OPTIONS_MAX).flatMap(numOptions -> {
            List<VoteOption> voteOptions = IntStream.range(0, numOptions)
                    .mapToObj(i -> new VoteOption())
                    .toList();

            Arbitrary<Integer> choiceLimitArbitrary = Arbitraries.integers().between(1, numOptions);
            int max = (1 << numOptions) - 1;
            return choiceLimitArbitrary.flatMap(choiceLimit -> {
                Arbitrary<Integer> voteValueArbitrary = Arbitraries.integers()
                        .between(0, 0);
                return voteValueArbitrary.map(voteValue -> Tuple.of(voteOptions, choiceLimit, voteValue));
            });
        });
    }

    @Provide
    Arbitrary<Tuple.Tuple3<List<VoteOption>, Integer, Integer>> voteDataWithInvalidVoteValue() {
        return Arbitraries.integers().between(Poll.VOTE_OPTIONS_MIN, Poll.VOTE_OPTIONS_MAX).flatMap(numOptions -> {
            List<VoteOption> voteOptions = IntStream.range(0, numOptions)
                    .mapToObj(i -> new VoteOption())
                    .toList();

            Arbitrary<Integer> choiceLimitArbitrary = Arbitraries.integers().between(1, numOptions);
            int max = (1 << numOptions) - 1;
            return choiceLimitArbitrary.flatMap(choiceLimit -> {
                Arbitrary<Integer> voteValueArbitrary = Arbitraries.integers()
                        .filter(value -> value < 0 || value > max);
                return voteValueArbitrary.map(voteValue -> Tuple.of(voteOptions, choiceLimit, voteValue));
            });
        });
    }
}
