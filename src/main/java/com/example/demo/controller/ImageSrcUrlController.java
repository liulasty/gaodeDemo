package com.example.demo.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.dto.CommonResponse;
import com.example.demo.dto.page.PageQuery;
import com.example.demo.dto.page.PageResult;
import com.example.demo.imagelistcrawler.ImageSrcCrawlRecord;
import com.example.demo.imagelistcrawler.ImageSrcUrl;
import com.example.demo.imagelistcrawler.ImageSrcUrlDetail;
import com.example.demo.service.impl.ImageSrcUrlServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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


    @PostMapping("/saveDetail")
    public CommonResponse<String> saveImageSrcUrlDetail(@RequestBody List<ImageSrcUrlDetail> imageSrcUrl) {
        imageSrcUrlService.saveImageSrcDetail(imageSrcUrl);
        return new CommonResponse<>(200, "保存成功", "保存成功");
    }

    /**
     * 获取图片源爬取记录列表
     *
     * @param page 页码
     *  @param           size 每页大小
     *  @param           keyword 关键字
     * @returns 分页结果
     */
    @GetMapping("/crawl-records")
    public PageResult<ImageSrcCrawlRecord> getCrawlRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        // 分页初始化
        Page<ImageSrcCrawlRecord> pageInfo = new Page<>(page, size);
        
        // 构建查询条件
        QueryWrapper<ImageSrcCrawlRecord> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like("alt", keyword);
        }
        
        // 执行查询并转换结果
        IPage<ImageSrcCrawlRecord> pageResult = imageSrcUrlService.pageCrawlRecords(pageInfo, wrapper);

        
        return new PageResult<>(pageResult, pageResult.getRecords());
    }

    /**
     * 获取爬取记录详情及关联的URL列表
     * @param id 记录ID
     * @param params 查询参数
     */
    @GetMapping("/crawl-records/{id}")
    public PageResult getCrawlRecordDetail(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
         PageResult<ImageSrcUrlDetail> pageResult = imageSrcUrlService.pageCrawlRecordDetail(id, page, size);

        
        return pageResult;
    }

    /**
     * 删除爬取记录及关联的URL
     * @param id 爬取记录ID
     */
    @DeleteMapping("/crawl-records/{id}")
    public Map<String, Object> deleteCrawlRecord(@PathVariable Long id) {
        // 删除记录及关联URL
        int deletedCount = imageSrcUrlService.deleteRecordWithUrls(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", deletedCount >= 0);
        result.put("deletedCount", deletedCount);
        return result;
    }

    /**
     * 批量删除URL
     * @param body 请求体
     *
     */
    @DeleteMapping("/urls/batch")
    public Map<String, Object> batchDeleteUrls(
            @RequestBody Map<String, Object> body) {
        Long recordId = Long.parseLong(body.get("recordId").toString());
        List<Long> ids = ((List<?>) body.get("ids")).stream()
                .map(id -> Long.parseLong(id.toString()))
                .collect(Collectors.toList());
        
        int deletedCount = imageSrcUrlService.batchDeleteUrls(recordId, ids);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", deletedCount > 0);
        result.put("deletedCount", deletedCount);
        return result;
    }

    /**
     * 获取URL详情
     * @param id URLID
     */
    @GetMapping("/urls/{id}")
    public Map<String, Object> getUrlDetail(@PathVariable Long id) {
        ImageSrcUrlDetail url = imageSrcUrlService.getUrlDetail(id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", url.getId());
        result.put("tid", url.getTid());
        result.put("title", url.getAlt());
        result.put("index", url.getImageIndex());
        result.put("src", url.getSrc());
        result.put("alt", url.getAlt());
        result.put("attributes", url.getAttributes());
        result.put("createdTime", url.getCreatedTime());
        result.put("updatedTime", url.getUpdatedTime());
        return result;
    }

    /**
     * 更新URL
     * @param id URLID
     * @param data 更新数据
     */
    @PatchMapping("/urls/{id}")
    public Map<String, Object> updateUrl(
            @PathVariable Long id,
            @RequestBody Map<String, Object> data) {
        ImageSrcUrlDetail url = new ImageSrcUrlDetail();
        url.setId(id);
        if (data.containsKey("alt")) {
            url.setAlt(data.get("alt").toString());
        }
        if (data.containsKey("attributes")) {
            url.setAttributes(data.get("attributes").toString());
        }
        
        boolean success = imageSrcUrlService.updateUrl(url);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("updatedTime", url.getUpdatedTime());
        return result;
    }
}

