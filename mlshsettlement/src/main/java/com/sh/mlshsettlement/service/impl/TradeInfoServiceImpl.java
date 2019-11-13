package com.sh.mlshsettlement.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sh.mlshcommon.util.ListUtil;
import com.sh.mlshsettlement.model.TradeInfo;
import com.sh.mlshsettlement.mapper.TradeInfoMapper;
import com.sh.mlshsettlement.service.ITradeInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sh.mlshsettlement.vo.SummaryPlateVO;
import com.sh.mlshsettlement.vo.SummaryTradeInfoVO;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 商户提现明细表 服务实现类
 * </p>
 *
 * @author jinghui.yu
 * @since 2019-11-05
 */
@Service
public class TradeInfoServiceImpl extends ServiceImpl<TradeInfoMapper, TradeInfo> implements ITradeInfoService {

    @Override
    public List<SummaryTradeInfoVO> summaryTradeInfo(LocalDate day,Long id) {
        return baseMapper.summaryTradeInfo(day,id);
    }

    @Override
    public int updateSummaryTradeInfo(List<Long> id, Long batchNo, Integer status) {
        UpdateWrapper<TradeInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().in(TradeInfo::getId,id);
        TradeInfo tradeInfo = new TradeInfo(batchNo,status);
        return baseMapper.update(tradeInfo,updateWrapper);
    }

    @Override
    public List<SummaryPlateVO> summaryPlateTradeInfo(LocalDate day) {
        return baseMapper.summaryPlateTradeInfo(day);
    }

    @Override
    public int updateTradeInfoStatus(Long batchNo, Integer code) {
        TradeInfo tradeInfo = new TradeInfo(batchNo, code);
        LambdaUpdateWrapper<TradeInfo> eq = new UpdateWrapper<TradeInfo>().lambda().eq(TradeInfo::getBatchNo, batchNo);
        return baseMapper.update(tradeInfo,eq);
    }
}
