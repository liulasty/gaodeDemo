package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName("t_permission")
public class Permission {
    @TableId
    private Long id;
    private String name;
    private String path;
    private String method;
    private String description;
}