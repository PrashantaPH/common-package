package com.common.exception.handler;

import com.common.dto.ApiResponse;
import com.common.exception.ApplicationException;
import com.common.exception.InvalidAuthException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

import static com.common.utils.CommonUtil.errorObject;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiResponse<Object>> handleTechnicalException(ApplicationException exception) {
        ApiResponse<Object> response = errorObject(exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.status(500).body(response);
    }

    @ExceptionHandler({InvalidAuthException.class})
    public ResponseEntity<ApiResponse<Object>> invalidAuthException(HttpServletRequest req, InvalidAuthException ex) {
        ApiResponse<Object> apiResponse = errorObject(ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFound(UsernameNotFoundException exception) {
        ApiResponse<Object> response = errorObject(HttpStatus.BAD_REQUEST.name(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handle404(NoHandlerFoundException ex) {
        Map<String, Object> details = java.util.Map.of("method", ex.getHttpMethod(), "path", ex.getRequestURL());
        return ResponseEntity.status(404)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorObject("NOT_FOUND", "Endpoint not found", details));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handle405(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        Map<String, Object> details = java.util.Map.of("path", req.getRequestURI(), "method", ex.getMethod(), "message", ex.getMessage());
        return ResponseEntity.status(405)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorObject("METHOD_NOT_ALLOWED", "HTTP method not allowed", details));
    }

    // If static resource mappings are enabled:
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest req) {
        Map<String, Object> details = java.util.Map.of("path", req.getRequestURI(), "message", ex.getMessage());
        return ResponseEntity.status(404)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorObject("NOT_FOUND", "Endpoint not found", details));
    }

}
