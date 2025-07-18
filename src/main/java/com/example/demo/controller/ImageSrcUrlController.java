package com.example.demo.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.CommonResponse;
import com.example.demo.dto.page.PageQuery;
import com.example.demo.dto.page.PageResult;
import com.example.demo.imagelistcrawler.ImageSrcUrl;
import com.example.demo.service.impl.ImageSrcUrlServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Administrator
 */
@RestController
@RequestMapping("/api/imageSrcUrl")
@RequiredArgsConstructor
@Slf4j
public class ImageSrcUrlController {

    private final ImageSrcUrlServiceImpl imageSrcUrlService;

    @PostMapping("/save")
    public CommonResponse<String> saveImageSrcUrl(@RequestBody List<ImageSrcUrl> imageSrcUrl) {
        imageSrcUrlService.saveImageSrcUrl(imageSrcUrl);

        return new CommonResponse<>(200, "保存成功", "保存成功");
    }

    @PostMapping("/list")
    public List<ImageSrcUrl> getImageSrcUrlList() {
        return imageSrcUrlService.list();
    }


    @PostMapping("/delete")
    public CommonResponse<String> deleteImageSrcUrl (@RequestBody List<Long> imageSrcUrl) {
        imageSrcUrlService.removeBatchByIds(imageSrcUrl);
        return new CommonResponse<>(200, "删除成功", "删除成功");
    }


    @PostMapping("/page")
    public PageResult<ImageSrcUrl> getImageSrcUrlPage( @RequestBody PageQuery<ImageSrcUrl> pageParam) {
        Page<ImageSrcUrl> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());
        LambdaQueryWrapper<ImageSrcUrl> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(pageParam.getParams().getAlt() != null && !pageParam.getParams().getAlt().isEmpty(),ImageSrcUrl::getAlt, pageParam.getParams().getAlt());
        IPage<ImageSrcUrl> pageResult = imageSrcUrlService.page(page,wrapper);
        return new PageResult<>(pageResult, pageResult.getRecords());
    }
}
