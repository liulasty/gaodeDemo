package com.example.demo.jmsdemo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 */
@Data
@TableName("t_message")
public class MessageFragment implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String messageId;
    private String deviceId;
    private String dataType;
    private int totalFragments;
    private int fragmentIndex;
    private String payload;
    private long timestamp = System.currentTimeMillis();
}