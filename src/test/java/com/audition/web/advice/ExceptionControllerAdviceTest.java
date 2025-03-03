package com.audition.web.advice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.client.HttpClientErrorException;

class ExceptionControllerAdviceTest {

    @Mock
    private AuditionLogger auditionLogger;

    private ExceptionControllerAdvice advice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        advice = new ExceptionControllerAdvice(auditionLogger);
    }

    @Test
    void handleHttpClientException() {
        HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");
        ProblemDetail result = advice.handleHttpClientException(exception);

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatus());
        assertEquals("400 Bad Request", result.getDetail());
        assertEquals("API Error Occurred", result.getTitle());
    }

    @Test
    void handleMainException() {
        Exception exception = new RuntimeException("Unexpected error");
        ProblemDetail result = advice.handleMainException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getStatus());
        assertEquals("Unexpected error", result.getDetail());
        assertEquals("Unexpected Error", result.getTitle());

        verify(auditionLogger).logErrorWithException(any(), eq("Unhandled exception occurred"), eq(exception));
        verify(auditionLogger).logStandardProblemDetail(any(), eq(result), eq(exception));
    }

    @Test
    void handleSystemException() {
        SystemException exception = new SystemException("System error", "System Error Title", 400);
        ProblemDetail result = advice.handleSystemException(exception);

        assertEquals(400, result.getStatus());
        assertEquals("System error", result.getDetail());
        assertEquals("System Error Title", result.getTitle());

        verify(auditionLogger).logErrorWithException(any(), eq("SystemException occurred"), eq(exception));
        verify(auditionLogger).logStandardProblemDetail(any(), eq(result), eq(exception));
    }

    @Test
    void handleSystemExceptionWithInvalidStatusCode() {
        SystemException exception = new SystemException("Invalid status", "Invalid Status", 999);
        ProblemDetail result = advice.handleSystemException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getStatus());
        assertEquals("Invalid status", result.getDetail());
        assertEquals("Invalid Status", result.getTitle());

        verify(auditionLogger).info(any(), contains("Error Code from Exception could not be mapped to a valid HttpStatus Code"));
    }

    @Test
    void handleHttpRequestMethodNotSupportedException() {
        HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException("POST");
        ProblemDetail result = advice.handleMainException(exception);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), result.getStatus());
        assertEquals("API Error Occurred", result.getTitle());
    }

    @Test
    void handleExceptionWithNullMessage() {
        Exception exception = new Exception();
        ProblemDetail result = advice.handleMainException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getStatus());
        assertEquals("API Error occurred. Please contact support or administrator.", result.getDetail());
        assertEquals("Unexpected Error", result.getTitle());
    }
}
