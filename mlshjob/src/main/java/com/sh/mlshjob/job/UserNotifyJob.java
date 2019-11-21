package com.sh.mlshjob.job;

import com.sh.mlshsettlement.service.ISettleFlowingService;
import com.tuan.job.util.AbstractJobRunnable;
import com.tuan.job.util.ExecutorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用户通知接口
 */
@Component("userNotifyJob")
public class UserNotifyJob extends AbstractJobRunnable {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ISettleFlowingService settleFlowingService;

    @Override
    public void run() {
        log.info("用户打款通知接口开始执行,LogID={}", logId);
        try {
            synchronized(this) {
                settleFlowingService.userNotify();
            }
            log.info("用户打款通知接口执行完成,回调JobCenter完成,logID={}", logId);
            ExecutorManager.callBack(logId, "日数据汇总执行完成", 1);
        }catch (Exception ex){
            log.error("用户打款通知接口回调JobCenter出错,LogID="+logId, ex);
            try {
                ExecutorManager.callBack(logId, "查询打款结果执行完成",0);
            } catch (Exception e){
                log.error("用户打款通知接口回调JobCenter出错,LogID="+logId, e);
            }
        }
    }
}
