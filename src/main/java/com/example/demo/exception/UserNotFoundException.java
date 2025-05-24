package com.example.demo.exception;

/*
 * Created with IntelliJ IDEA.
 * @Author: lz
 * @Date: 2025/04/13/20:16
 * @Description:
 */

/**
 * @author lz
 */
public class UserNotFoundException extends IllegalArgumentException {
    
    public UserNotFoundException(String message) {
        super(message);
    }
}