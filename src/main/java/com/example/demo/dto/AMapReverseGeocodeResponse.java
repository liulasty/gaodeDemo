package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.apache.catalina.connector.Response;

/**
 * @author lz
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AMapReverseGeocodeResponse {
    private String status;
    private String info;
    private Regeocode regeocode;

    @Data
    public static class Regeocode {
        private String formatted_address;
        private AddressComponent addressComponent;
    }

    @Data
    public static class AddressComponent {
        private String country;
        private String province;
        private String city;
        private String citycode;
        private String district;
        private String adcode;
        private String township;
        private StreetNumber streetNumber;
    }

    @Data
    public static class StreetNumber {
        private String street;
        private String number;
        private String location;
    }
}