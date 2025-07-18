package com.example.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.imagelistcrawler.ImageSrcUrl;
import com.example.demo.mapper.ImageSrcUrlMapper;
import com.example.demo.service.ImageSrcUrlService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Administrator
 */
@Service
public class ImageSrcUrlServiceImpl  extends ServiceImpl<ImageSrcUrlMapper, ImageSrcUrl> implements ImageSrcUrlService {


    @Override
    public void saveImageSrcUrl(List<ImageSrcUrl> imageSrcUrl) {
        saveBatch(imageSrcUrl);
    }
}
