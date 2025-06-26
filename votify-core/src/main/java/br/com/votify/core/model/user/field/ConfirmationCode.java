package br.com.votify.core.model.user.field;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Objects;

@Getter
@EqualsAndHashCode
public final class ConfirmationCode {
    public static final int CODE_LENGTH = 100;
    public static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";

    private final String value;

    public ConfirmationCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }
        this.value = code.toString();
    }

    private ConfirmationCode(String value) {
        this.value = value;
    }

    public static ConfirmationCode parseUnsafe(String value) {
        return new ConfirmationCode(value);
    }

    public boolean matches(String code) {
        return Objects.equals(value, code);
    }

    public String encodeToUrlCode() {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
