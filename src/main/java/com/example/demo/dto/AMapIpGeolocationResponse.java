package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;

@Data
@TableName("t_amap_ip_geolocation") 
public class AMapIpGeolocationResponse {
    @TableId(type = IdType.AUTO) 
    private Long id;

    @JsonProperty("status")
    private String status;

    @JsonProperty("info")
    private String info;

    @JsonProperty("infocode")
    private String infoCode;

    @JsonProperty("province")
    private String province;

    @JsonProperty("city")
    private String city;

    @JsonProperty("adcode")
    private String adCode;

    @JsonProperty("rectangle")
    private String rectangle;
}