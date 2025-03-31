package com.example.demo.util;

import cn.hutool.core.lang.UUID;

import com.alibaba.fastjson2.JSONWriter;
import com.example.demo.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class FileStorageUtil {
    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file, Long userId) throws FileStorageException {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFilename);
        String fileName = UUID.randomUUID() + fileExtension;

        Path userDir = Paths.get(uploadDir, userId.toString());
        try {
            if (!Files.exists(userDir)) {
                Files.createDirectories(userDir);
            }

            Path targetLocation = userDir.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return targetLocation.toString();
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName, ex);
        }
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}