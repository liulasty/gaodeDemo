package com.example.demo.update;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.nio.file.*;


public class GeoIPDatabaseUpdater {

    private static final String LICENSE_KEY = "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";
    private static final String DB_URL = "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-City&license_key=" + LICENSE_KEY + "&suffix=tar.gz";
    private static final String DB_DIR = "data";
    private static final String DB_PATH = DB_DIR + "/GeoLite2-City.mmdb";

//    @Scheduled(cron = "0 0 0 * * ?")
    public void updateDatabase() throws IOException {
        Files.createDirectories(Paths.get(DB_DIR));
        Path tempFile = Paths.get(DB_DIR + "/temp.tar.gz");

        try {
            // 下载文件
            try (InputStream in = new URL(DB_URL).openStream()) {
                Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // 解压文件
            try (TarArchiveInputStream tarInput = new TarArchiveInputStream(
                    new GzipCompressorInputStream(new FileInputStream(tempFile.toFile())))) {

                TarArchiveEntry entry;
                while ((entry = tarInput.getNextTarEntry()) != null) {
                    if (entry.getName().endsWith(".mmdb")) {
                        Path outputPath = Paths.get(DB_DIR, entry.getName().substring(entry.getName().lastIndexOf("/") + 1));
                        Files.copy(tarInput, outputPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
