package com.example.demo.dto;

/*
 * Created with IntelliJ IDEA.
 * @Author: lz
 * @Date: 2025/04/13/20:18
 * @Description:
 */

import lombok.Getter;

import java.util.Map;

/**
 * @author lz
 */
@Getter
public class SanitizedUser {
    private final String username;
    private final Map<String, Object> attributes;

    public SanitizedUser(String username, Map<String, Object> attributes) {
        this.username = username;
        this.attributes = attributes;
    }
    
}