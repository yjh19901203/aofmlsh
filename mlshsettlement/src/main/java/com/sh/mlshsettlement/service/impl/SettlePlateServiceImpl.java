package com.sh.mlshsettlement.service.impl;

import com.sh.mlshcommon.util.ListUtil;
import com.sh.mlshcommon.util.ThreadPoolUtil;
import com.sh.mlshsettlement.model.SettlePlate;
import com.sh.mlshsettlement.mapper.SettlePlateMapper;
import com.sh.mlshsettlement.service.ISettlePlateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sh.mlshsettlement.service.ITradeInfoService;
import com.sh.mlshsettlement.vo.SummaryPlateVO;
import com.sh.mlshsettlement.vo.SummaryTradeInfoVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jinghui.yu
 * @since 2019-11-05
 */
@Service
public class SettlePlateServiceImpl extends ServiceImpl<SettlePlateMapper, SettlePlate> implements ISettlePlateService {

    @Resource
    private ITradeInfoService tradeInfoService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void summaryPlateSettle(LocalDate day) {
        //汇总订单数据
        List<SummaryPlateVO> summaryPlateVOS = tradeInfoService.summaryPlateTradeInfo(day);
        if(ListUtil.isNull(summaryPlateVOS)){
            return;
        }
        //添加汇总数据
        baseMapper.batchInsertOrUpdate(summaryPlateVOS);
    }
}
