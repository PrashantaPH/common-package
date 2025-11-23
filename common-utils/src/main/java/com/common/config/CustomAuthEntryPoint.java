package com.common.config;

import com.common.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.common.utils.CommonUtil.errorObject;
import static com.common.utils.CommonUtil.toJson;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.warn("Authentication failed for request: {} {}, Error: {}",
                request.getMethod(), request.getRequestURI(), authException.getMessage());

        String errorCode = determineErrorCode(authException);
        String errorMessage = determineErrorMessage(authException, errorCode);

        ApiResponse<Object> apiResponse = buildErrorResponse(errorCode, errorMessage, request, authException);

        configureResponse(response);
        response.getWriter().write(toJson(apiResponse));
    }

    private ApiResponse<Object> buildErrorResponse(String errorCode, String errorMessage,
                                                   HttpServletRequest request,
                                                   AuthenticationException authException) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now().toString());
        errorDetails.put("path", request.getRequestURI());
        errorDetails.put("method", request.getMethod());
        errorDetails.put("exception", authException.getClass().getSimpleName());

        if (isDevelopmentEnvironment()) {
            errorDetails.put("debugMessage", authException.getMessage());
            errorDetails.put("stackTrace", getStackTrace(authException));
        }
        return errorObject(errorCode, errorMessage, errorDetails);
    }

    private String determineErrorCode(AuthenticationException authException) {
        String exceptionName = authException.getClass().getSimpleName();

        return switch (exceptionName) {
            case "BadCredentialsException" -> "AUTH_001";
            case "InsufficientAuthenticationException" -> "AUTH_002";
            case "DisabledException" -> "AUTH_003";
            case "LockedException" -> "AUTH_004";
            case "AccountExpiredException" -> "AUTH_005";
            case "CredentialsExpiredException" -> "AUTH_006";
            case "JwtException" -> "AUTH_007";
            case "InvalidBearerTokenException" -> "AUTH_008";
            default -> "AUTH_000";
        };
    }

    private String determineErrorMessage(AuthenticationException authException, String errorCode) {
        return switch (errorCode) {
            case "AUTH_001" -> "Invalid username or password";
            case "AUTH_002" -> "Insufficient authentication details";
            case "AUTH_003" -> "Account is disabled";
            case "AUTH_004" -> "Account is locked";
            case "AUTH_005" -> "Account has expired";
            case "AUTH_006" -> "Credentials have expired";
            case "AUTH_007" -> "Invalid or malformed JWT token";
            case "AUTH_008" -> "Invalid bearer token format";
            default -> "Unauthorized access - Authentication required";
        };
    }

    private void configureResponse(HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding("UTF-8");

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
    }

    private boolean isDevelopmentEnvironment() {
        String env = System.getProperty("spring.profiles.active", "development");
        return "development".equals(env) || "dev".equals(env);
    }

    private String getStackTrace(AuthenticationException exception) {
        StackTraceElement[] stackTrace = exception.getStackTrace();
        if (stackTrace.length > 0) {
            return stackTrace[0].toString(); // Return first stack trace element
        }
        return "No stack trace available";
    }
}
