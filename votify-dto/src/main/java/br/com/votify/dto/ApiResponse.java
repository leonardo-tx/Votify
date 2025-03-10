package br.com.votify.dto;

import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String errorCode;
    private final String errorMessage;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    public static <T> ApiResponse<T> error(VotifyException exception) {
        return new ApiResponse<>(
            false,
            null,
            exception.getErrorCode().getMessageKey(),
            exception.getMessage()
        );
    }
}
