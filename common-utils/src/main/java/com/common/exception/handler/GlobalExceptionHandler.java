package com.common.exception.handler;

import com.common.dto.ApiResponse;
import com.common.exception.ApplicationException;
import com.common.exception.InvalidAuthException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.common.utils.CommonUtil.errorObject;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiResponse<Object>> handleTechnicalException(ApplicationException exception) {
        ApiResponse<Object> response = errorObject(exception.getMessage(), exception.getErrorCode());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler({InvalidAuthException.class})
    public ResponseEntity<ApiResponse<Object>> invalidAuthException(HttpServletRequest req, InvalidAuthException ex) {
        ApiResponse<Object> apiResponse = errorObject(ex.getErrorCode(), ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFound(UsernameNotFoundException exception) {
        ApiResponse<Object> response = errorObject(exception.getMessage(), HttpStatus.BAD_REQUEST.name());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
