package com.example.demo.imagelistcrawler;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("t_image_src_url_detail")
public class ImageSrcUrlDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private long tid;

    private Long imageIndex;

    private String src;

    private String alt;

    private String attributes;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;
}
