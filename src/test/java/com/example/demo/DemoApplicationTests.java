package com.example.demo;

import com.example.demo.service.GeoLocationService;
import com.example.demo.service.ImageCrawlerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.demo.update.GeoIPDatabaseUpdater;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() throws Exception {

        GeoIPDatabaseUpdater geoIPDatabaseUpdater = new GeoIPDatabaseUpdater();

        geoIPDatabaseUpdater.updateDatabase();


    }

    @Test
    void testGeoLocationService() throws Exception {

        GeoLocationService geoLocationService = new GeoLocationService();

        String location = geoLocationService.getLocation("183.195.88.75");

        System.out.println(location);

    }
    
    @Test
    void downloadImage() throws Exception {

        ImageCrawlerService iMageCrawlerService = new ImageCrawlerService();
        String saveDirectory = "./downloads";
        String targetUrl = "https://example.cohttps://everia.club/2025/04/05/emiri-yamashita-%e5%b1%b1%e4%b8%8b%e3%82%a8%e3%83%9f%e3%83%aa%e3%83%bc-1st%e5%86%99%e7%9c%9f%e9%9b%86-%e3%80%8eshy%e3%80%8f-set-03/";
        iMageCrawlerService.crawlImages(saveDirectory,targetUrl);
        
        System.out.println("Image download complete."+iMageCrawlerService.getDownloadedUrls());
        
        
    }

}