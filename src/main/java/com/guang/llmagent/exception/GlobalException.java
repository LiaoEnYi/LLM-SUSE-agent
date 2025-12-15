package com.guang.llmagent.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Author L.
 * @Date 2025/12/9 10:21
 * @Description 全局异常捕获
 * @Version 1.0
 */
@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(AgentStateException.class)
    public String handleException(AgentStateException e) {
        return e.getMessage();
    }
}
