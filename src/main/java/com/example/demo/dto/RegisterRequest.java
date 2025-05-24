package com.example.demo.dto;


/**
 * 注册请求
 * 
 * @author lz
 */
public record RegisterRequest(
        String firstname,
        String lastname,
        String email,
        String password
) {}