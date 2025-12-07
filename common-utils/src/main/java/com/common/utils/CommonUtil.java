package com.common.utils;

import com.common.dto.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import static com.common.utils.Constants.FAILURE;
import static com.common.utils.Constants.SUCCESS;

public class CommonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private CommonUtil() {}

    public static String toJson(Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    public static ApiResponse<Object> errorObject(String errorCode, String message) {
        return ApiResponse.builder()
                .apiStatus(FAILURE)
                .apiVersion("1.0")
                .errorCode(errorCode)
                .message(message)
                .build();
    }

    public static ApiResponse<Object> errorObject(String errorCode, String message, Map<String, Object> errorDetails) {
        return ApiResponse.builder()
                .apiStatus(FAILURE)
                .apiVersion("1.0")
                .errorCode(errorCode)
                .message(message)
                .data(errorDetails)
                .build();
    }

    public static ApiResponse<Object> successObject(String message) {
        return ApiResponse.builder()
                .apiStatus(SUCCESS)
                .apiVersion("1.0")
                .message(message)
                .build();
    }

    public static ApiResponse<Object> successObject(String message, Object data) {
        return ApiResponse.builder()
                .apiStatus(SUCCESS)
                .apiVersion("1.0")
                .message(message)
                .data(data)
                .build();
    }
}
