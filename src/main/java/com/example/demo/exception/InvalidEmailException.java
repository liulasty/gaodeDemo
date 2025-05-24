package com.example.demo.exception;

/*
 * Created with IntelliJ IDEA.
 * @Author: lz
 * @Date: 2025/04/13/20:19
 * @Description:
 */

/**
 * @author lz
 */
public class InvalidEmailException extends IllegalArgumentException {
    public InvalidEmailException(String s) {
        super(s);
    }
}