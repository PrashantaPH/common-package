package com.common.utils;

import java.util.List;

public class Constants {

    private Constants() {}


    public static final String VALID = "VALID";
    public static final String EXPIRED = "EXPIRED";

    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";

    public static final String BEARER = "Bearer ";
    public static final String AUTHORIZATION = "Authorization";

    /* Error Codes */
    public static final String AUTH_500 = "AUTH_500";
    public static final String AUTH_401 = "AUTH_401";
    public static final String TOKEN_401 = "TOKEN_401";
    public static final String APIKEY_401 = "APIKEY_401";

    public static final List<String> PUBLIC_ENDPOINTS = List.of("/api/users/register", "/api/users/by-email", "/api/authentication/login");

    public static final String MAI_WEB_CLIENT_RETRY_COUNT = "webclient.retry-count";
    public static final String MAI_WEB_CLIENT_RETRY_TIMEOUT_IN_MILLISECONDS = "webclient.retry-timeout-in-ms";
    public static final String MAI_WEB_CLIENT_RETRY_INTERVAL_IN_MILLISECONDS = "webclient.retry-interval-in-ms";
    public static final String MAI_WEB_CLIENT_MAX_DOWNLOAD_SIZE_IN_MB = "webclient.max-download-size-in-mb";

    public static final int DEFAULT_WEB_CLIENT_RETRY_COUNT = 3;
    public static final int DEFAULT_WEB_CLIENT_RETRY_INTERVAL_IN_MILLISECONDS = 10000;
    public static final int DEFAULT_WEB_CLIENT_RETRY_TIMEOUT_IN_MILLISECONDS = 20000;
    public static final int DEFAULT_WEB_CLIENT_MAX_DOWNLOAD_SIZE_IN_MB = 10;
}
