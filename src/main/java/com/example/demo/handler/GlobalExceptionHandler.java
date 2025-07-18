package com.example.demo.handler;

import com.example.demo.dto.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;

/**
 * @author lz
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public CommonResponse<?> handleRuntimeException(RuntimeException e) {
        log.error("服务器内部错误: {}", e.getMessage());
        return new CommonResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误", null);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public CommonResponse<?> handleWebClientException(WebClientResponseException e) {
        return new CommonResponse<>(e.getStatusCode().value(), e.getResponseBodyAsString(), Collections.emptyMap());
    }
}