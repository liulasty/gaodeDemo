package com.example.demo.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.CommonResponse;
import com.example.demo.dto.page.PageQuery;
import com.example.demo.dto.page.PageResult;
import com.example.demo.imagelistcrawler.ImageSrcUrl;
import com.example.demo.service.impl.ImageSrcUrlServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

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
    public CommonResponse<String> deleteImageSrcUrl(@RequestBody List<Long> imageSrcUrl) {
        imageSrcUrlService.removeBatchByIds(imageSrcUrl);
        return new CommonResponse<>(200, "删除成功", "删除成功");
    }


    @PostMapping("/page")
    public PageResult<ImageSrcUrl> getImageSrcUrlPage(@RequestBody PageQuery<ImageSrcUrl> pageParam) {
        // 参数校验
        if (pageParam == null) {
            throw new IllegalArgumentException("分页参数不能为空");
        }

        // 分页初始化
        Page<ImageSrcUrl> page = new Page<>(pageParam.getPageNum(), pageParam.getPageSize());

        // 构建查询条件
        QueryWrapper<ImageSrcUrl> wrapper = new QueryWrapper<>();
        if (pageParam.getParams() != null && StringUtils.isNotBlank(pageParam.getParams().getAlt())) {
            wrapper.like("alt", pageParam.getParams().getAlt());
        }

        // 排序字段白名单校验
        String orderBy = pageParam.getOrderBy();
        boolean isValidOrderField = isValidOrderByField(orderBy);
        if (isValidOrderField && pageParam.getOrder() != null && !pageParam.getOrder().isEmpty()) {
            boolean isAsc = "asc".equalsIgnoreCase(pageParam.getOrder());
            wrapper.orderBy(!orderBy.isEmpty(), isAsc, orderBy);
        }else {
            wrapper.orderByDesc("timestamp");
        }

        // 执行查询
        IPage<ImageSrcUrl> pageResult = imageSrcUrlService.page(page, wrapper);
        return new PageResult<>(pageResult, pageResult.getRecords());
    }

    /**
     * 校验排序字段是否在白名单中
     */
    private boolean isValidOrderByField(String field) {
        if (StringUtils.isBlank(field)) {
            return false;
        }
        Set<String> allowedFields = Set.of("id", "alt", "create_time", "update_time","href");
        return allowedFields.contains(field);
    }
}

