package com.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

@Data
@JsonPropertyOrder({"result", "active", "access_token", "expires_in", "refresh_token", "refresh_expires_in"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private String result;

    private boolean active;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("expires_in")
    private long expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("refresh_expires_in")
    private long refreshExpiresIn;
}
