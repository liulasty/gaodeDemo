package com.example.demo.service;


import com.example.demo.imagelistcrawler.ImageSrcUrl;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author Administrator
 */
public interface ImageSrcUrlService {
    void saveImageSrcUrl(List<ImageSrcUrl> imageSrcUrl);
}
