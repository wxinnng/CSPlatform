package com.csplatform.course.handler;

import com.csplatform.common.exception.BusinessException;
import com.csplatform.common.resp.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author WangXing
 * @Date 2026/1/11 12:49
 * @PackageName:com.csplatform.course.handler
 * @ClassName: GlobalExceptionHandler
 * @Version 1.0
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result<String> exceptionHandler(Exception e){
        log.error(e.getMessage());
        return Result.fail(e.getMessage());
    }


}
