package com.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

import static com.common.utils.Constants.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CommonAppConfig {

    private final Environment environment;

    @Bean
    public WebClient webClient() {
        int retryCount = environment.getProperty(MAI_WEB_CLIENT_RETRY_COUNT, Integer.class, DEFAULT_WEB_CLIENT_RETRY_COUNT);
        int retryIntervalInMillis = environment.getProperty(MAI_WEB_CLIENT_RETRY_INTERVAL_IN_MILLISECONDS, Integer.class, DEFAULT_WEB_CLIENT_RETRY_INTERVAL_IN_MILLISECONDS);
        int retryTimeoutInMillis = environment.getProperty(MAI_WEB_CLIENT_RETRY_TIMEOUT_IN_MILLISECONDS, Integer.class, DEFAULT_WEB_CLIENT_RETRY_TIMEOUT_IN_MILLISECONDS);
        int maxDownloadSizeInMb = environment.getProperty(MAI_WEB_CLIENT_MAX_DOWNLOAD_SIZE_IN_MB, Integer.class, DEFAULT_WEB_CLIENT_MAX_DOWNLOAD_SIZE_IN_MB);

        return WebClient.builder()
                .clientConnector(httpConnector(retryTimeoutInMillis))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchangeStrategies(exchangeStrategies(maxDownloadSizeInMb))
                .filters(filters -> filters.addAll(List.of(errorHandler(), fixedRetryHandler(retryCount, retryIntervalInMillis))))
                .build();
    }

    public static ReactorClientHttpConnector httpConnector(int retryTimeoutInMillis) {
        Duration duration = Duration.ofMillis(retryTimeoutInMillis);
        ConnectionProvider connectionProvider = ConnectionProvider.builder("custom")
                .maxIdleTime(duration.plus(duration))
                .maxLifeTime(duration.plus(duration))
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .responseTimeout(Duration.ofMillis(retryTimeoutInMillis));
        return new ReactorClientHttpConnector(httpClient);
    }

    public static ExchangeFilterFunction fixedRetryHandler(int retryCount, int retryIntervalInMillis) {
        return (request, next) -> next.exchange(request)
                .retryWhen(Retry.fixedDelay(retryCount, Duration.ofMillis(retryIntervalInMillis))
                        .doBeforeRetry(retrySignal -> log.info("Before retry request :: Count: {}", retrySignal.totalRetries() + 1))
                        .doAfterRetry(retrySignal -> log.info("After retry request :: Count {}", retrySignal.totalRetries() + 1)));
    }

    public static ExchangeFilterFunction errorHandler() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                return clientResponse
                        .bodyToMono(String.class)
                        .switchIfEmpty(Mono.just(clientResponse.statusCode().toString()))
                        .flatMap(errorBody -> {
                            log.error("WebClient error: {}", errorBody);
                            return Mono.error(new ApplicationContextException(errorBody));
                        });
            } else {
                return Mono.just(clientResponse);
            }
        });
    }

    public static ExchangeStrategies exchangeStrategies(int maxDownloadSizeInMb) {
        return ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(maxDownloadSizeInMb * 1024 * 1024))
                .build();
    }
}
