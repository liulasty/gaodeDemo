package com.example.demo.imagelistcrawler;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 图片目录源链接
 * @author: liuzhen
 * @date: 2023/9/27
 */
@Data
@TableName("t_image_src_url")
public class ImageSrcUrl {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String alt;
    private String href;
    private String page;
    private String src;

    private long timestamp = System.currentTimeMillis();
}
