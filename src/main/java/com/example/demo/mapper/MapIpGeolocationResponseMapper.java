package com.example.demo.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.dto.AMapIpGeolocationResponse;
import org.apache.ibatis.annotations.Mapper;


/**
 * 高德地图 IP 地理定位响应映射器
 *
 * @author lz
 * @date 2025/04/13 16:19:19
 */
@Mapper
public interface MapIpGeolocationResponseMapper extends BaseMapper<AMapIpGeolocationResponse> {
}