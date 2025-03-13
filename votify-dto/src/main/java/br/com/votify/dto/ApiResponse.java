package br.com.votify.dto;

import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String errorCode;
    private String errorMessage;

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
