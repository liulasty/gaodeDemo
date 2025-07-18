package com.example.demo.jmsdemo;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.*;

/**
 * @author Administrator
 */
@Data
@TableName("t_data_timeout_record")
public class TimeoutRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String groupId;
    private String deviceId;
    private String messageId;
    private Long expireTime;
    private Long cleanupTime;
}
