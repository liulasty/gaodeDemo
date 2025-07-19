package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.dto.page.PageResult;
import com.example.demo.imagelistcrawler.ImageSrcCrawlRecord;
import com.example.demo.imagelistcrawler.ImageSrcUrl;
import com.example.demo.imagelistcrawler.ImageSrcUrlDetail;
import com.example.demo.mapper.ImageSrcCrawlRecordMapper;
import com.example.demo.mapper.ImageSrcUrlDetailMapper;
import com.example.demo.mapper.ImageSrcUrlMapper;
import com.example.demo.service.ImageSrcUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Administrator
 */
@Service
public class ImageSrcUrlServiceImpl  extends ServiceImpl<ImageSrcUrlMapper, ImageSrcUrl> implements ImageSrcUrlService {

    @Autowired
    private ImageSrcUrlDetailMapper imageSrcUrlDetailMapper;

    @Autowired
    private ImageSrcCrawlRecordMapper imageSrcCrawlRecordMapper;


    @Override
    public void saveImageSrcUrl(List<ImageSrcUrl> imageSrcUrl) {
        saveBatch(imageSrcUrl);
    }

    @Override
    @Transactional
    public void saveImageSrcDetail(List<ImageSrcUrlDetail> imageSrcUrlDetail) {

        ImageSrcUrlDetail detail = imageSrcUrlDetail.get(0);
        ImageSrcCrawlRecord imageSrcCrawlRecord = new ImageSrcCrawlRecord();
        imageSrcCrawlRecord.setTitle(detail.getTitle());
        imageSrcCrawlRecord.setHref(detail.getSrc());
        imageSrcCrawlRecord.setImageSum((long) imageSrcUrlDetail.size());

        //保存此次爬取到的图片list解析记录
        baseMapper.insertCrawlRecords(imageSrcCrawlRecord);

        imageSrcUrlDetailMapper.insertList(imageSrcUrlDetail,imageSrcCrawlRecord.getId());

    }

    @Override
    public IPage<ImageSrcCrawlRecord> pageCrawlRecords(Page<ImageSrcCrawlRecord> pageInfo, QueryWrapper<ImageSrcCrawlRecord> wrapper) {

        return imageSrcCrawlRecordMapper.selectPage(pageInfo, wrapper);
    }

    @Override
    public PageResult<ImageSrcUrlDetail> pageCrawlRecordDetail(Long id, int page, int size) {
        Page<ImageSrcUrlDetail> pageInfo = new Page<>(page, size);
        QueryWrapper<ImageSrcUrlDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("tid", id);
        IPage<ImageSrcUrlDetail> urlPage = imageSrcUrlDetailMapper.selectPage(pageInfo, wrapper);
        return new PageResult<>(urlPage, urlPage.getRecords());
    }

    @Override
    public int deleteRecordWithUrls(Long id) {
        return 0;
    }

    @Override
    public int batchDeleteUrls(Long recordId, List<Long> ids) {
            return 0;
    }

    @Override
    public ImageSrcUrlDetail getUrlDetail(Long id) {
            return null;
    }

    @Override
    public boolean updateUrl(ImageSrcUrlDetail url) {
        return false;
    }
}
