package com.jackson.handler;

import com.jackson.exception.BaseException;
import com.jackson.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    public Result ExceptionHandler(BaseException be) {
        log.info("异常信息:{}", be.getMessage());
        return Result.error(be.getMessage());
    }
}
