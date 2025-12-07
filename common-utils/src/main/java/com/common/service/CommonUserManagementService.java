package com.common.service;

import com.common.dto.CommonUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import static com.common.utils.Constants.API_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommonUserManagementService {

    private final WebClient webClient;

    @Value("${app.api-key}")
    private String appApiKey;

    @Value("${byEmail}")
    private String byEmail;

    @Value("${userManagementBaseUrl}")
    private String userManagementBaseUrl;

    public CommonUserDetails findByEmail(String email) {
        String uri = UriComponentsBuilder.fromUriString(userManagementBaseUrl)
                .path(byEmail)
                .queryParam("email", email)
                .toUriString();

        return webClient.get()
                .uri(uri)
                .header(API_KEY, appApiKey)
                .retrieve()
                .bodyToMono(CommonUserDetails.class)
                .block();
    }

}
