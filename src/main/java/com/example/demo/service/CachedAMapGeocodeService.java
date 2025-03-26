package com.example.demo.service;

import com.example.demo.dto.GeocodeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CachedAMapGeocodeService {

    private final AMapGeocodeService geocodeService;
    private final CacheManager cacheManager;

    @Cacheable(
            value = "geocodeCache",
            key = "#longitude + '|' + #latitude",
            unless = "#result == null || #result.isEmpty()",
            cacheManager = "cacheManager"
    )
    public GeocodeResponse getCachedAddress(double longitude, double latitude) {
        log.info("未命中缓存，实际调用高德API: {},{}", longitude, latitude);
        return geocodeService.getFormattedAddress(longitude, latitude);
    }

}
