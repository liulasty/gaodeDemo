package com.example.demo.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.page.PageResult;
import com.example.demo.imagelistcrawler.ImageSrcCrawlRecord;
import com.example.demo.imagelistcrawler.ImageSrcUrl;
import com.example.demo.imagelistcrawler.ImageSrcUrlDetail;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author Administrator
 */
public interface ImageSrcUrlService {
    void saveImageSrcUrl(List<ImageSrcUrl> imageSrcUrl);


    void saveImageSrcDetail(List<ImageSrcUrlDetail> imageSrcUrlDetail);

    IPage<ImageSrcCrawlRecord> pageCrawlRecords(Page<ImageSrcCrawlRecord> pageInfo, QueryWrapper<ImageSrcCrawlRecord> wrapper);

    PageResult<ImageSrcUrlDetail> pageCrawlRecordDetail(Long id, int page, int size);

    int deleteRecordWithUrls(Long id);

    int batchDeleteUrls(Long recordId, List<Long> ids);

    ImageSrcUrlDetail getUrlDetail(Long id);

    boolean updateUrl(ImageSrcUrlDetail url);
}
