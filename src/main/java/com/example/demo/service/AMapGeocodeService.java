package com.example.demo.service;

import ch.qos.logback.classic.encoder.JsonEncoder;
import com.alibaba.fastjson.support.hsf.HSFJSONUtils;
import com.example.demo.dto.AMapReverseGeocodeResponse;
import com.example.demo.dto.GeocodeResponse;
import com.example.demo.dto.gaode.AddressComponent;
import com.example.demo.dto.gaode.Poi;
import com.example.demo.dto.gaode.Regeocode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;
import java.util.Map;

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
    public GeocodeResponse getFormattedAddress(double longitude, double latitude) {

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://restapi.amap.com/v3/geocode/regeo?key=" + amapApiKey +
                "&radius=1000&location=" + longitude + "," + latitude +
                "&extensions=all&roadlevel=0";


        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        log.info("正在请求高德逆向地理编码服务，位置: {}", response);

        GeocodeResponse geocodeResponse = new GeocodeResponse();

        // 转换代码
        ObjectMapper mapper = new ObjectMapper();
        if (response != null && "1".equals(response.get("status"))) {
            Map<String, Object> regeocodeMap = (Map<String, Object>) response.get("regeocode");
            Regeocode regeocode = new Regeocode();
            if (regeocodeMap != null && regeocodeMap.get("pois") != null) {
                regeocode.setPois((List<Poi>) regeocodeMap.get("pois"));
            }

            if (regeocodeMap != null && regeocodeMap.get("addressComponent") != null) {

                Map<String, Object> addressComponent = (Map<String, Object>) regeocodeMap.get("addressComponent");
                if (addressComponent == null) {
                    log.warn("高德API返回地址信息为空");
                    return null;
                }
                AddressComponent component = new AddressComponent();
                component.setCountry(addressComponent.get("country").toString());
                component.setProvince(addressComponent.get("province").toString());
                component.setCity(addressComponent.get("city").toString());
                component.setDistrict(addressComponent.get("district").toString());
                component.setTownship(addressComponent.get("township").toString());
                component.setTowncode(addressComponent.get("towncode").toString());
                component.setAdcode(addressComponent.get("adcode").toString());
                component.setCitycode(addressComponent.get("citycode").toString());
                regeocode.setAddressComponent(component);
            }
            if (regeocodeMap != null && regeocodeMap.get("formatted_address") != null){

                regeocode.setFormatted_address(regeocodeMap.get("formatted_address").toString());
            }
            geocodeResponse.setRegeocode(regeocode);
            return geocodeResponse;
        }

        return null;
    }

}
