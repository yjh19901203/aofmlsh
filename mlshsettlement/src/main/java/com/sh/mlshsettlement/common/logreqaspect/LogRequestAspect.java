package com.sh.mlshsettlement.common.logreqaspect;

import com.alibaba.fastjson.JSONObject;
import com.sh.mlshcommon.util.JSONUtil;
import com.sh.mlshcommon.util.ThreadPoolUtil;
import com.sh.mlshsettlement.common.LogModel;
import com.sh.mlshsettlement.common.logaspect.LogAnnotion;
import com.sh.mlshsettlement.model.SettleReqLog;
import com.sh.mlshsettlement.service.ISettleReqLogService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class LogRequestAspect {

    @Resource
    private ISettleReqLogService settleReqLogService;

    @Pointcut("@annotation(LogRequestAnnotion)")
    public void pointCut(){

    }

    @Around("pointCut()")
    public Object around(JoinPoint joinPoint)throws Throwable{
        LogModel logModel = null;
        int type = 0;
        String keyField = "";
        String request = "";
        String response = "";
        String exception = "";
        SettleReqLog settleReqLog = new SettleReqLog(type,keyField,request,response,exception);
        try{
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            LogRequestAnnotion annotation = method.getAnnotation(LogRequestAnnotion.class);
            settleReqLog.setType(annotation.type());
            settleReqLog.setKeyField(annotation.key());
            logModel = LogModel.newLogModel(method.getName());
            Object[] args = joinPoint.getArgs();
            ParameterNameDiscoverer parms = new DefaultParameterNameDiscoverer();
            String[] parameterNames = parms.getParameterNames(method);
            String requestParm = "";
            for (int i = 0; i < parameterNames.length; i++) {
                requestParm = requestParm + parameterNames[i] + "=" + args[i] + ";";
            }
            settleReqLog.setRequest(requestParm);
            logModel.addData("start",request);
            Object obj = ((ProceedingJoinPoint) joinPoint).proceed();
            response = obj==null?"": JSONUtil.toString(obj);
            settleReqLog.setResponse(response);
            return obj;
        }catch(Throwable e){
            settleReqLog.setExceptionMsg(e.getMessage());
            if(logModel!=null){
                logModel.addData("exception",e.getMessage());
            }
            log.error("异常",e);
            throw e;
        }finally {
            if(logModel!=null){
                log.info(logModel.toJson(true));
            }
            ThreadPoolUtil.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        settleReqLogService.save(settleReqLog);
                    }catch(Exception e){
                        log.error("添加请求日志异常",e);
                    }

                }
            });
        }
    }
}
