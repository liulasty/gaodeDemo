package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class ImageCrawlerService {

    @Value("${crawler.target.url}")
    private String targetUrl;

    @Value("${crawler.save.directory}")
    private String saveDirectory;

    @Value("${crawler.allowed.extensions}")
    private String[] allowedExtensions;

    private Set<String> downloadedUrls = new HashSet<>();

    // 每天凌晨3点执行
    @Scheduled(cron = "0 0 3 * * ?")
    public void crawlImages() {
        try {
            log.info("开始爬取图片: {}", targetUrl);

            // 创建保存目录
            Path savePath = Paths.get(saveDirectory);
            if (!Files.exists(savePath)) {
                Files.createDirectories(savePath);
            }

            // 获取网页内容
            Document doc = Jsoup.connect(targetUrl).get();

            // 选择所有图片标签
            Elements imgElements = doc.select("img[src]");

            int downloadedCount = 0;
            for (Element img : imgElements) {
                String imgUrl = img.absUrl("src");

                // 检查是否已下载过
                if (downloadedUrls.contains(imgUrl)) {
                    continue;
                }

                // 检查文件扩展名
                if (isAllowedExtension(imgUrl)) {
                    try {
                        downloadImage(imgUrl, savePath.toString());
                        downloadedUrls.add(imgUrl);
                        downloadedCount++;
                        log.info("下载成功: {}", imgUrl);
                    } catch (IOException e) {
                        log.error("下载失败: {}", imgUrl, e);
                    }
                }
            }

            log.info("爬取完成，共下载 {} 张新图片", downloadedCount);
        } catch (IOException e) {
            log.error("爬取过程中发生错误", e);
        }
    }

    private boolean isAllowedExtension(String url) {
        for (String ext : allowedExtensions) {
            if (url.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    private void downloadImage(String imageUrl, String saveDir) throws IOException {
        String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
        File outputFile = new File(saveDir, fileName);

        // 使用Apache Commons IO简化下载过程
        org.apache.commons.io.FileUtils.copyURLToFile(
                new URL(imageUrl),
                outputFile,
                10000,  // 10秒连接超时
                60000   // 60秒读取超时
        );
    }
}
