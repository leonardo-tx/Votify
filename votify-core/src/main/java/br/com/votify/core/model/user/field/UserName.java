package br.com.votify.core.model.user.field;

import br.com.votify.core.utils.CharacterUtils;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class UserName {
    public static final int MIN_LENGTH = 3;
    public static final int MAX_LENGTH = 40;

    private String value;

    public UserName(String userName) throws VotifyException {
        if (userName == null) {
            throw new VotifyException(VotifyErrorCode.USER_NAME_EMPTY);
        }
        if (userName.length() < MIN_LENGTH || userName.length() > MAX_LENGTH) {
            throw new VotifyException(VotifyErrorCode.USER_NAME_INVALID_LENGTH, MIN_LENGTH, MAX_LENGTH);
        }
        if (userName.charAt(0) == '-' || userName.charAt(userName.length() - 1) == '-') {
            throw new VotifyException(VotifyErrorCode.USER_NAME_INVALID);
        }
        if (userNameHasInvalidChars(userName)) {
            throw new VotifyException(VotifyErrorCode.USER_NAME_INVALID_CHARACTER);
        }
        value = userName;
    }

    private boolean userNameHasInvalidChars(String userName) {
        for (int i = 0; i < userName.length(); i++) {
            char c = userName.charAt(i);
            if (c == '-' || CharacterUtils.isOneByteDigit(c) || CharacterUtils.isOneByteLowercaseLetter(c)) {
                continue;
            }
            return true;
        }
        return false;
    }

    public static UserName parseUnsafe(String userName) {
        UserName userNameObj = new UserName();
        userNameObj.value = userName;

        return userNameObj;
    }
}
