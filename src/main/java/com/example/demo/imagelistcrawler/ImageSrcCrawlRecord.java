package com.example.demo.imagelistcrawler;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalTime;

/**
 * @author Administrator
 */
@Data
@TableName("t_image_src_crawl_recored")
public class ImageSrcCrawlRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String href;

    private Long ImageSum;

    private LocalTime  createdTime;
}
