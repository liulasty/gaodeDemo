package com.example.demo.service;

import com.example.demo.dto.AMapIpGeolocationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * 高德地图 IP 地理定位服务
 *
 * @author lz
 * @date 2025/04/13 16:09:08
 */
@Service
@RequiredArgsConstructor
public class AMapIpGeolocationService {

    // 用于发起HTTP请求的WebClient实例
    private final WebClient amapWebClient;
    // 高德地图API密钥
    private final String amapApiKey;

    /**
     * 通过IP地址获取地理定位信息
     * @param ip 要定位的IP地址（可选，如果为null，则使用请求方的IP）
     * @return 包含地理定位响应的Mono对象
     */
    public Mono<AMapIpGeolocationResponse> locateByIp(String ip) {
        // 初始化HTTP GET请求
        WebClient.RequestHeadersUriSpec<?> request = amapWebClient.get();

        // 构建请求URI，包括API密钥作为查询参数
        WebClient.RequestHeadersSpec<?> uri = request.uri(uriBuilder -> {
            uriBuilder.path("/v3/ip")
                    .queryParam("key", amapApiKey);

            // 如果提供了IP参数，则将其作为查询参数添加
            if (ip != null && !ip.isEmpty()) {
                uriBuilder.queryParam("ip", ip);
            }

            return uriBuilder.build();
        });

        // 获取响应
        WebClient.ResponseSpec responseSpec = uri.retrieve();
        // 将响应转换为Mono<AMapIpGeolocationResponse>
        Mono<AMapIpGeolocationResponse> responseMono = responseSpec.bodyToMono(AMapIpGeolocationResponse.class);

        // 设置5秒的请求超时时间
        Mono<AMapIpGeolocationResponse> timeoutMono = responseMono.timeout(Duration.ofSeconds(5));

        // 配置重试机制，最多重试3次，每次间隔100毫秒
        Retry retrySpec = Retry.backoff(3, Duration.ofMillis(100));
        return timeoutMono.retryWhen(retrySpec);
    }

    /**
     * 通过IP地址获取省份信息
     * @param ip IP地址（可选）
     * @return 省份名称或未找到时返回空字符串
     */
    public String getProvinceByIp(String ip) {
        // 获取通过IP定位的响应
        Mono<AMapIpGeolocationResponse> responseMono = locateByIp(ip);

        // 从响应中提取省份信息
        Mono<String> resultMono = responseMono.map(response -> {
            // 检查响应状态，如果成功则返回省份名称
            if ("1".equals(response.getStatus())) {
                return response.getProvince();
            }
            // 如果请求失败，则抛出异常
            throw new RuntimeException("高德地图API请求失败: " + response.getInfo());
        });

        // 返回省份名称或在发生错误时返回空字符串
        return resultMono.onErrorReturn("").block();
    }

    /**
     * 通过IP地址获取城市信息
     * @param ip IP地址（可选）
     * @return 城市名称或未找到时返回空字符串
     */
    public String getCityByIp(String ip) {
        // 获取通过IP定位的响应
        Mono<AMapIpGeolocationResponse> responseMono = locateByIp(ip);

        // 从响应中提取城市信息
        Mono<String> resultMono = responseMono.map(response -> {
            // 检查响应状态，如果成功则返回城市名称
            if ("1".equals(response.getStatus())) {
                return response.getCity();
            }
            // 如果请求失败，则抛出异常
            throw new RuntimeException("高德地图API请求失败: " + response.getInfo());
        });

        // 返回城市名称或在发生错误时返回空字符串
        return resultMono.onErrorReturn("").block();
    }

    /**
     * 获取完整的地理位置信息（省份+城市）
     * @param ip IP地址（可选）
     * @return 格式化的地理位置字符串或未找到时返回空字符串
     */
    public String getFullLocationByIp(String ip) {
        // 获取通过IP定位的响应
        Mono<AMapIpGeolocationResponse> responseMono = locateByIp(ip);

        // 从响应中提取完整的地理位置信息
        Mono<String> resultMono = responseMono.map(response -> {
            // 检查响应状态，如果成功则拼接省份和城市名称
            if ("1".equals(response.getStatus())) {
                return response.getProvince() + response.getCity();
            }
            // 如果请求失败，则抛出异常
            throw new RuntimeException("高德地图API请求失败: " + response.getInfo());
        });

        // 返回完整的地理位置字符串或在发生错误时返回空字符串
        return resultMono.onErrorReturn("").block();
    }
}