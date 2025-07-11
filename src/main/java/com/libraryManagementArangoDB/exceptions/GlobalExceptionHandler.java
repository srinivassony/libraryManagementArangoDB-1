package com.libraryManagementArangoDB.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchApiException.class)
    public Map<String, String> handleNoSuchApiException(NoSuchApiException ex) {
        return Map.of("error", "API Not Found", "message", ex.getMessage());
    }
}
