package br.com.votify.api.handlers;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> globalExceptionHandler(
        Exception e,
        WebRequest request
    ) {
        logger.error("Error:", e);
        VotifyException exception = new VotifyException(VotifyErrorCode.INTERNAL);
        return ApiResponse.error(exception).createResponseEntity();
    }

    @ExceptionHandler(VotifyException.class)
    public ResponseEntity<ApiResponse<Object>> globalVotifyExceptionHandler(
        VotifyException e,
        WebRequest request
    ) {
        return ApiResponse.error(e).createResponseEntity();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> globalHttpMessageNotReadableExceptionHandler(
        HttpMessageNotReadableException e,
        WebRequest request
    ) {
        VotifyException exception = new VotifyException(VotifyErrorCode.BAD_REQUEST);
        return ApiResponse.error(exception).createResponseEntity();
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> globalNoResourceFoundExceptionHandler(
            NoResourceFoundException e,
            WebRequest request
    ) {
        VotifyException exception = new VotifyException(VotifyErrorCode.ENDPOINT_NOT_FOUND);
        return ApiResponse.error(exception).createResponseEntity();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> globalHttpRequestMethodNotSupportedExceptionHandler(
            HttpRequestMethodNotSupportedException e,
            WebRequest request
    ) {
        VotifyException exception = new VotifyException(VotifyErrorCode.METHOD_NOT_ALLOWED);
        return ApiResponse.error(exception).createResponseEntity();
    }

    @ExceptionHandler(BeanCreationException.class)
    public ResponseEntity<ApiResponse<Object>> globalBeanCreationExceptionHandler(
        BeanCreationException e,
        WebRequest request
    ) {
        if (e.getRootCause() instanceof VotifyException votifyException) {
            return ApiResponse.error(votifyException).createResponseEntity();
        }
        logger.error("Error:", e);
        VotifyException exception = new VotifyException(VotifyErrorCode.INTERNAL);
        return ApiResponse.error(exception).createResponseEntity();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> globalMethodArgumentTypeMismatchExceptionHandler(
        MethodArgumentTypeMismatchException e,
        WebRequest request
    ) {
        return ApiResponse.error(new VotifyException(VotifyErrorCode.BAD_REQUEST)).createResponseEntity();
    }
}
