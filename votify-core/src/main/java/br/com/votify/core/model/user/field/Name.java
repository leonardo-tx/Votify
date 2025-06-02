package br.com.votify.core.model.user.field;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Name {
    public static final int MIN_LENGTH = 1;
    public static final int MAX_LENGTH = 50;

    private String value;

    public Name(String name) throws VotifyException {
        if (name == null) {
            throw new VotifyException(VotifyErrorCode.NAME_EMPTY);
        }
        if (name.length() < MIN_LENGTH || name.length() > MAX_LENGTH) {
            throw new VotifyException(VotifyErrorCode.NAME_INVALID_LENGTH, MIN_LENGTH, MAX_LENGTH);
        }
        if (nameHasInvalidChars(name)) {
            throw new VotifyException(VotifyErrorCode.NAME_INVALID);
        }
        value = name;
    }

    private static boolean nameHasInvalidChars(String name) {
        if (name.charAt(0) == ' ' || name.charAt(name.length() - 1) == ' ') {
            return true;
        }
        int whitespaceSequence = 0;
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isISOControl(c)) {
                return true;
            }
            if (c != ' ') {
                if (Character.isWhitespace(c)) return true;

                whitespaceSequence = 0;
                continue;
            }
            whitespaceSequence += 1;
            if (whitespaceSequence > 1) {
                return true;
            }
        }
        return false;
    }

    public static Name parseUnsafe(String name) {
        Name nameObj = new Name();
        nameObj.value = name;

        return nameObj;
    }
}
