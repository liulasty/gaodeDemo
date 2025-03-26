package com.example.demo.service;

import ch.qos.logback.classic.encoder.JsonEncoder;
import com.alibaba.fastjson.support.hsf.HSFJSONUtils;
import com.example.demo.dto.AMapReverseGeocodeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;


import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class AMapGeocodeService {

    private final WebClient amapWebClient;  // WebClient实例
    private final String amapApiKey;       // 高德地图API密钥

    /**
     * 逆向地理编码服务
     * @param longitude 经度
     * @param latitude 纬度
     * @return 包含地理编码结果的Mono对象
     */
    public Mono<AMapReverseGeocodeResponse> reverseGeocode(double longitude, double latitude) {
        // 1. 构建请求URI
        WebClient.RequestHeadersUriSpec<?> requestSpec = amapWebClient.get();
        WebClient.RequestHeadersSpec<?> uri = requestSpec.uri(uriBuilder -> {

            UriBuilder builder = uriBuilder.path("/v3/geocode/regeo")
                    .queryParam("key", amapApiKey)
                    .queryParam("radius", 1000)
                    .queryParam("location", longitude + "," + latitude)
                    .queryParam("extensions", "all")
                    .queryParam("roadlevel", 0);
            return builder.build();
        });

        // 2. 执行请求
        WebClient.ResponseSpec responseSpec = uri.retrieve();


        // 3. 解析响应体
        Mono<AMapReverseGeocodeResponse> responseMono =
                responseSpec.bodyToMono(AMapReverseGeocodeResponse.class).doOnNext(response -> log.info("API Response: {}", response));






        return responseMono.timeout(Duration.ofSeconds(5));
    }

    /**
     * 获取格式化地址
     * @param longitude 经度
     * @param latitude 纬度
     * @return 格式化地址字符串
     */
    public String getFormattedAddress(double longitude, double latitude) {
        try {
            // 1. 调用逆向地理编码
            Mono<AMapReverseGeocodeResponse> responseMono = reverseGeocode(longitude, latitude);

            log.info("正在请求高德逆向地理编码服务，位置: {}, {}", longitude, latitude);
            // 使用block()同步获取响应（适用于WebClient）
            AMapReverseGeocodeResponse response = responseMono.block();
            if (response == null) {
                throw new RuntimeException("高德API未返回有效响应");
            }
            if ("1".equals(response.getStatus())) {
                return response.getRegeocode().getFormatted_address();
            } else {
                log.warn("高德API返回非成功状态: {} - {}", response.getStatus(), response.getInfo());
                throw new RuntimeException("高德API错误: " + response.getInfo());
            }

//            RestTemplate restTemplate = new RestTemplate();
//            String url = "https://restapi.amap.com/v3/geocode/regeo?key=" + amapApiKey + "&radius=1000&location=" + longitude + "," + latitude + "&extensions=all&roadlevel=0";
//            Response responseR = restTemplate.getForObject(url, Response.class);
//            if (responseR != null && "1".equals(response.getStatus())) {
//                return response.getRegeocode().getFormatted_address();
//            } else {
//                String info = response != null ? response.getInfo() : "无响应";
//                log.warn("高德API返回非成功状态: {}", info);
//                throw new RuntimeException("高德API错误: " + info);
//            }

//            // 3. 设置回退值
//            Mono<String> withFallback = resultMono.onErrorReturn("未知位置");
//
//            // 4. 阻塞获取结果
//            return withFallback.block();
        } catch (Exception e) {
            log.error("获取格式化地址失败，位置 {}, {}: {}", longitude, latitude, e.getMessage());
            return "未知位置";
        }
    }

}
