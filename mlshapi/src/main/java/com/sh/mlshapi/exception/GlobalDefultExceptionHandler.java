package com.sh.mlshapi.exception;

import com.sh.mlshcommon.vo.ResultVO;
import com.sh.mlshsettlement.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.UnexpectedTypeException;

@Slf4j
@RestControllerAdvice
public class GlobalDefultExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultVO defaultExceptionHandler(Exception e){
        log.error("程序异常",e);
        if(e instanceof BusinessException){
            return ResultVO.error("业务异常："+e.getMessage());
        }

        if(e instanceof BindException){
            BindException bindException = (BindException) e;
            BindingResult bindingResult = bindException.getBindingResult();
            FieldError fieldError = bindingResult.getFieldError();
            String field = fieldError.getField();
            String defaultMessage = fieldError.getDefaultMessage();
            return ResultVO.error("参数校验失败："+field+"="+defaultMessage);
        }
        if(e instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) e;
            BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
            FieldError fieldError = bindingResult.getFieldError();
            String field = fieldError.getField();
            String defaultMessage = fieldError.getDefaultMessage();
            return ResultVO.error("参数校验失败："+field+"="+defaultMessage);
        }
        return ResultVO.error(e.getMessage());
    }

    @ExceptionHandler(UnexpectedTypeException.class)
    @ResponseBody
    public ResultVO validExceptionHandler(UnexpectedTypeException e){
        log.error("参数校验异常",e);
        return ResultVO.error(e.getMessage());
    }
}
