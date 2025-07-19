package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.imagelistcrawler.ImageSrcCrawlRecord;
import com.example.demo.imagelistcrawler.ImageSrcUrl;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

import java.util.List;

/**
 * @author Administrator
 */
public interface ImageSrcUrlMapper  extends BaseMapper<ImageSrcUrl> {

    @Insert("INSERT INTO t_image_src_crawl_record (title,href,image_sum) " +
        "VALUES (#{title},#{href},#{imageSum})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Long insertCrawlRecords(ImageSrcCrawlRecord imageSrcCrawlRecord);

}
