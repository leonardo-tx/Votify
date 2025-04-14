package br.com.votify.api.handlers;

import static org.junit.jupiter.api.Assertions.*;

import br.com.votify.core.utils.exceptions.VotifyErrorCode;
import br.com.votify.core.utils.exceptions.VotifyException;
import br.com.votify.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.reflect.Method;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @Test
    public void testGlobalExceptionHandler() {
        Exception exception = new Exception("Test exception");
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.globalExceptionHandler(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(VotifyErrorCode.INTERNAL.getMessageKey(), response.getBody().getErrorCode());
    }

    @Test
    public void testGlobalVotifyExceptionHandler() {
        VotifyException exception = new VotifyException(VotifyErrorCode.BAD_REQUEST);
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.globalVotifyExceptionHandler(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(VotifyErrorCode.BAD_REQUEST.getMessageKey(), response.getBody().getErrorCode());
    }

    @Test
    public void testGlobalHttpMessageNotReadableExceptionHandler() {
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Bad request");
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.globalHttpMessageNotReadableExceptionHandler(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(VotifyErrorCode.BAD_REQUEST.getMessageKey(), response.getBody().getErrorCode());
    }

    @Test
    public void testGlobalNoResourceFoundExceptionHandler() {
        NoResourceFoundException exception = new NoResourceFoundException(HttpMethod.GET, "/users");
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.globalNoResourceFoundExceptionHandler(exception, webRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(VotifyErrorCode.ENDPOINT_NOT_FOUND.getMessageKey(), response.getBody().getErrorCode());
    }

    @Test
    public void testGlobalHttpRequestMethodNotSupportedExceptionHandler() {
        HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException("Method not allowed");
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.globalHttpRequestMethodNotSupportedExceptionHandler(exception, webRequest);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(VotifyErrorCode.METHOD_NOT_ALLOWED.getMessageKey(), response.getBody().getErrorCode());
    }

    @Test
    public void testGlobalBeanCreationExceptionHandlerWithVotifyException() {
        VotifyException rootCause = new VotifyException(VotifyErrorCode.BAD_REQUEST);
        BeanCreationException exception = new BeanCreationException("Bean creation failed", rootCause);

        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.globalBeanCreationExceptionHandler(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(VotifyErrorCode.BAD_REQUEST.getMessageKey(), response.getBody().getErrorCode());
    }

    @Test
    public void testGlobalBeanCreationExceptionHandlerWithOtherException() {
        Exception rootCause = new Exception("Internal error");
        BeanCreationException exception = new BeanCreationException("Bean creation failed", rootCause);

        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.globalBeanCreationExceptionHandler(exception, webRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(VotifyErrorCode.INTERNAL.getMessageKey(), response.getBody().getErrorCode());
    }

    @Test
    public void testGlobalMethodArgumentTypeMismatchExceptionHandler() throws NoSuchMethodException {
        Object value = "invalidValue";
        Class<?> requiredType = Integer.class;
        String name = "parameterName";
        Method method = this.getClass().getDeclaredMethod("dummyMethod", Integer.class);
        MethodParameter param = new MethodParameter(method, 0);
        Throwable cause = new Throwable("Root cause");

        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(value, requiredType, name, param, cause);

        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.globalMethodArgumentTypeMismatchExceptionHandler(exception, webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(VotifyErrorCode.BAD_REQUEST.getMessageKey(), response.getBody().getErrorCode());
    }

    public void dummyMethod(Integer param) {
    }
}