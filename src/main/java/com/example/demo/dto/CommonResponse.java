package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回结构
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {
    /**
     * 状态码
     */
    private int code;
    /**
     * 提示信息
     */
    private String message;
    /**
     * 具体数据
     */
    private T data;
} 