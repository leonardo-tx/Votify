package br.com.votify.dto;

import br.com.votify.core.utils.exceptions.VotifyException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ApiResponse<T> {
    private boolean success;
    private T data;
    @JsonIgnore
    private HttpStatusCode status;
    private String errorCode;
    private String errorMessage;

    public static <T> ApiResponse<T> success(T data, HttpStatusCode status) {
        return new ApiResponse<>(
            true,
            data,
            status,
            null,
            null
        );
    }

    public static ApiResponse<Object> error(VotifyException exception) {
        return new ApiResponse<>(
            false,
            null,
            exception.getErrorCode().getHttpStatusCode(),
            exception.getErrorCode().getMessageKey(),
            exception.getMessage()
        );
    }

    public ResponseEntity<ApiResponse<T>> createResponseEntity() {
        return ResponseEntity.status(status).body(this);
    }
}
