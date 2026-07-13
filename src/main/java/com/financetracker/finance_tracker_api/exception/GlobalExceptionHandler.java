package com.financetracker.finance_tracker_api.exception;

import com.financetracker.finance_tracker_api.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<?> handleEmailExists(EmailAlreadyExistsException ex){

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status",409,
                        "error","Conflict",
                        "message",ex.getMessage()
                ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(
            BadCredentialsException ex
    ) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 401,
                        "error", "Unauthorized",
                        "message", "Invalid email or password"
                ));

    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(error);
    }

}
