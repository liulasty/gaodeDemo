package com.example.demo.service;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.InetAddress;

/**
 * 提供地理定位服务的类
 */
public class GeoLocationService {
    
    /**
     * 根据IP地址获取地理位置信息
     * 使用第三方API服务来获取地理位置信息
     *
     * @param ip 需要查询地理位置信息的IP地址
     * @return 地理位置信息的JSON字符串
     */
    public String getLocationByIp(String ip) {
        // 构造请求的URL
        String url = "http://ip-api.com/json/" + ip;
        // 创建RestTemplate对象用于发送HTTP请求
        RestTemplate restTemplate = new RestTemplate();
        // 发送GET请求并获取结果
        String result = restTemplate.getForObject(url, String.class);
        // 返回结果字符串
        return result;
    }

    // MaxMind GeoIP2数据库读取器
    private DatabaseReader dbReader;

    /**
     * GeoLocationService类的构造方法
     * 初始化时加载GeoIP2数据库
     * @throws Exception 如果数据库文件不存在或格式错误会抛出异常
     */
    public GeoLocationService() throws Exception {
        // 下载GeoIP2数据库文件并指定路径
        File database = new File("data/GeoLite2-City.mmdb");
        // 构建并初始化DatabaseReader对象
        dbReader = new DatabaseReader.Builder(database).build();
    }

    /**
     * 根据IP地址获取详细的地理位置信息
     * 使用MaxMind GeoIP2数据库来获取信息
     *
     * @param ip 需要查询地理位置信息的IP地址
     * @return 包含国家、城市、州/省和邮政编码的格式化字符串
     * @throws Exception 如果数据库查询失败会抛出异常
     */
    public String getLocation(String ip) throws Exception {
        // 将IP地址转换为InetAddress对象
        InetAddress ipAddress = InetAddress.getByName(ip);
        // 使用数据库读取器查询地理位置信息
        CityResponse response = dbReader.city(ipAddress);

        // 获取国家名称
        String country = response.getCountry().getName();
        // 获取城市名称
        String city = response.getCity().getName();
        // 获取邮政编码
        String postal = response.getPostal().getCode();
        // 获取最具体的次国家划分名称（如州、省）
        String state = response.getMostSpecificSubdivision().getName();

        // 格式化并返回地理位置信息
        return String.format("Country: %s, City: %s, State: %s, Postal: %s",
                country, city, state, postal);
    }
}