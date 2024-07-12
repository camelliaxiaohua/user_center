package camellia.exception;

import camellia.common.BaseResponse;
import camellia.common.ErrorCode;
import camellia.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 定义全局异常处理器 GlobalExceptionHandler，用于捕获和处理应用程序中抛出的异常，然后返回统一格式的错误响应。
 * @Datetime: 2024/7/12下午2:39
 * @author: Camellia.xioahua
 */

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHander {
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHander(BusinessException e) {
        //集中记录日志，进行处理
        log.error("businessException: " + e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e) {
        //集中记录日志，进行处理
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"");
    }
}
