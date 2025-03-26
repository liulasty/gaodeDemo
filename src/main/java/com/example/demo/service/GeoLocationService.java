package com.example.demo.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.InetAddress;

public class GeoLocationService {
    public String getLocationByIp(String ip) {
        String url = "http://ip-api.com/json/" + ip;
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(url, String.class);
        return result;
    }

    private DatabaseReader dbReader;

    public GeoLocationService() throws Exception {
        // 下载GeoIP2数据库文件并指定路径
        File database = new File("data/GeoLite2-City.mmdb");
        dbReader = new DatabaseReader.Builder(database).build();
    }

    public String getLocation(String ip) throws Exception {
        InetAddress ipAddress = InetAddress.getByName(ip);
        CityResponse response = dbReader.city(ipAddress);

        String country = response.getCountry().getName();
        String city = response.getCity().getName();
        String postal = response.getPostal().getCode();
        String state = response.getMostSpecificSubdivision().getName();

        return String.format("Country: %s, City: %s, State: %s, Postal: %s",
                country, city, state, postal);
    }
}
