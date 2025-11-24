package com.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CommonUserDetails {

    private String userId;

    private String fullName;

    private String role;

    private String status;

    private String email;

    private String password;
}
