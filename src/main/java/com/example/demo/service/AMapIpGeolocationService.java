package com.example.demo.service;

import com.example.demo.dto.AMapIpGeolocationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AMapIpGeolocationService {

    private final WebClient amapWebClient;
    private final String amapApiKey;

    /**
     * Get geolocation information by IP address
     * @param ip IP address to locate (optional, if null will use the requesting IP)
     * @return Mono containing the geolocation response
     */
    public Mono<AMapIpGeolocationResponse> locateByIp(String ip) {
        WebClient.RequestHeadersUriSpec<?> request = amapWebClient.get();

        WebClient.RequestHeadersSpec<?> uri = request.uri(uriBuilder -> {
            uriBuilder.path("/v3/ip")
                    .queryParam("key", amapApiKey);

            if (ip != null && !ip.isEmpty()) {
                uriBuilder.queryParam("ip", ip);
            }

            return uriBuilder.build();
        });

        WebClient.ResponseSpec responseSpec = uri.retrieve();
        Mono<AMapIpGeolocationResponse> responseMono = responseSpec.bodyToMono(AMapIpGeolocationResponse.class);

        Mono<AMapIpGeolocationResponse> timeoutMono = responseMono.timeout(Duration.ofSeconds(5));

        Retry retrySpec = Retry.backoff(3, Duration.ofMillis(100));
        return timeoutMono.retryWhen(retrySpec);
    }

    /**
     * Get province information by IP
     * @param ip IP address (optional)
     * @return Province name or empty string if not found
     */
    public String getProvinceByIp(String ip) {
        Mono<AMapIpGeolocationResponse> responseMono = locateByIp(ip);

        Mono<String> resultMono = responseMono.map(response -> {
            if ("1".equals(response.getStatus())) {
                return response.getProvince();
            }
            throw new RuntimeException("AMap API request failed: " + response.getInfo());
        });

        return resultMono.onErrorReturn("").block();
    }

    /**
     * Get city information by IP
     * @param ip IP address (optional)
     * @return City name or empty string if not found
     */
    public String getCityByIp(String ip) {
        Mono<AMapIpGeolocationResponse> responseMono = locateByIp(ip);

        Mono<String> resultMono = responseMono.map(response -> {
            if ("1".equals(response.getStatus())) {
                return response.getCity();
            }
            throw new RuntimeException("AMap API request failed: " + response.getInfo());
        });

        return resultMono.onErrorReturn("").block();
    }

    /**
     * Get full location information (province + city)
     * @param ip IP address (optional)
     * @return Formatted location string or empty string if not found
     */
    public String getFullLocationByIp(String ip) {
        Mono<AMapIpGeolocationResponse> responseMono = locateByIp(ip);

        Mono<String> resultMono = responseMono.map(response -> {
            if ("1".equals(response.getStatus())) {
                return response.getProvince() + response.getCity();
            }
            throw new RuntimeException("AMap API request failed: " + response.getInfo());
        });

        return resultMono.onErrorReturn("").block();
    }
}