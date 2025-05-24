package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_sys_token")
public class Token {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("token")
    private String token;

    @TableField("token_type")
    private String tokenType;

    @TableField("expired")
    private Boolean expired;

    @TableField("revoked")
    private Boolean revoked;

    @TableField("user_id")
    private Integer userId;
}