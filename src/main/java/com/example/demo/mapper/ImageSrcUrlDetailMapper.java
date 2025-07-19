package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.imagelistcrawler.ImageSrcUrlDetail;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ImageSrcUrlDetailMapper extends BaseMapper<ImageSrcUrlDetail> {



    void insertList(@Param("imageSrcUrlDetail") List<ImageSrcUrlDetail> imageSrcUrlDetail,@Param("id") Long id );
}
