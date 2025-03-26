package com.example.demo;

import com.example.demo.service.GeoLocationService;
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

}
