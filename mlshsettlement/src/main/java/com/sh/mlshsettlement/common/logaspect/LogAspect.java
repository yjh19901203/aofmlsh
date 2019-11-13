package com.sh.mlshsettlement.common.logaspect;

import com.alibaba.fastjson.JSONObject;
import com.sh.mlshsettlement.common.LogModel;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class LogAspect {
    @Pointcut("@annotation(LogAnnotion)")
    public void pointCut(){

    }

    @Around("pointCut()")
    public Object around(JoinPoint joinPoint)throws Throwable{
        LogModel logModel = null;
        try{
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            LogAnnotion annotation = method.getAnnotation(LogAnnotion.class);
            logModel = LogModel.newLogModel(method.getName());
            Object[] args = joinPoint.getArgs();
            logModel.addData("start",JSONObject.toJSONString(args));
            Object obj = ((ProceedingJoinPoint) joinPoint).proceed();
            if(annotation.result()){
                logModel.addData("result",JSONObject.toJSONString(obj));
            }
            return obj;
        }catch(Throwable e){
            if(logModel!=null){
                logModel.addData("exception",e.getMessage());
            }
            log.error("异常",e);
            throw e;
        }finally {
            if(logModel!=null){
                log.info(logModel.toJson(true));
            }
        }
    }
}
