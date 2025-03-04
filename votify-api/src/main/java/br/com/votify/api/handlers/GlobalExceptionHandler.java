package br.com.votify.api.handlers;

import br.com.votify.api.dto.ApiResponse;
import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> globalExceptionHandler(
        Exception e,
        WebRequest request
    ) {
        LOGGER.error("Error:", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(new VotifyException(VotifyErrorCode.INTERNAL)));
    }

    @ExceptionHandler(VotifyException.class)
    public ResponseEntity<ApiResponse<?>> globalVotifyExceptionHandler(
        VotifyException e,
        WebRequest request
    ) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatusCode())
            .body(ApiResponse.error(e));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> globalHttpMessageNotReadableExceptionHandler(
        HttpMessageNotReadableException e,
        WebRequest request
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(new VotifyException(VotifyErrorCode.BAD_REQUEST)));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<?>> globalNoResourceFoundExceptionHandler(
            NoResourceFoundException e,
            WebRequest request
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(new VotifyException(VotifyErrorCode.ENDPOINT_NOT_FOUND)));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<?>> globalHttpRequestMethodNotSupportedExceptionHandler(
            HttpRequestMethodNotSupportedException e,
            WebRequest request
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(new VotifyException(VotifyErrorCode.ENDPOINT_NOT_FOUND)));
    }

    @ExceptionHandler(BeanCreationException.class)
    public ResponseEntity<ApiResponse<?>> globalBeanCreationExceptionHandler(
        BeanCreationException e,
        WebRequest request
    ) {
        if (e.getRootCause() instanceof VotifyException votifyException) {
            return ResponseEntity.status(votifyException.getErrorCode().getHttpStatusCode())
                .body(ApiResponse.error(votifyException));
        }
        LOGGER.error("Error:", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(new VotifyException(VotifyErrorCode.INTERNAL)));
    }
}
