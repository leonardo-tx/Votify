package br.com.votify.dto;

import br.com.votify.core.utils.exceptions.VotifyException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String errorCode;
    private String errorMessage;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    public static <T> ApiResponse<T> error(VotifyException exception) {
        return new ApiResponse<>(false, null,
                exception.getErrorCode().getCode(), exception.getMessage());
    }
}
