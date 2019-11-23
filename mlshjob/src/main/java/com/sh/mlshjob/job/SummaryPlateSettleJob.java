package com.sh.mlshjob.job;

import com.sh.mlshcommon.util.DateUtil;
import com.sh.mlshsettlement.service.ISettleMchService;
import com.sh.mlshsettlement.service.ISettlePlateService;
import com.tuan.job.util.AbstractJobRunnable;
import com.tuan.job.util.ExecutorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

@Component("summaryPlateSettleJob")
public class SummaryPlateSettleJob extends AbstractJobRunnable {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ISettlePlateService settlePlateService;

    @Override
    public void run() {
        log.info("汇总平台打款数据开始执行,LogID={}", logId);
        try {
            synchronized(this) {
                LocalDate localDate = DateUtil.minuLocalDate(LocalDate.now(), 1);
                settlePlateService.summaryPlateSettle(localDate);
            }
            log.info("汇总平台打款数据执行完成,回调JobCenter完成,logID={}", logId);
            ExecutorManager.callBack(logId, "日数据汇总执行完成", 1);
        }catch (Exception ex){
            log.error("汇总平台打款数据回调JobCenter出错,LogID="+logId, ex);
            try {
                ExecutorManager.callBack(logId, "汇总平台打款数据执行完成",0);
            } catch (Exception e){
                log.error("汇总平台打款数据回调JobCenter出错,LogID="+logId, e);
            }
        }
    }
}
