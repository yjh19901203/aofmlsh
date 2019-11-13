package com.sh.mlshsettlement.service;

import com.sh.mlshsettlement.model.TradeInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sh.mlshsettlement.vo.SummaryPlateVO;
import com.sh.mlshsettlement.vo.SummaryTradeInfoVO;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 商户提现明细表 服务类
 * </p>
 *
 * @author jinghui.yu
 * @since 2019-11-05
 */
public interface ITradeInfoService extends IService<TradeInfo> {
    /**
     * 汇总交易
     * @param day
     * @return
     */
    List<SummaryTradeInfoVO> summaryTradeInfo(LocalDate day,Long id);

    int updateSummaryTradeInfo(List<Long> id,Long batchNo,Integer status);

    /**
     * 汇总交易
     * @param day
     * @return
     */
    List<SummaryPlateVO> summaryPlateTradeInfo(LocalDate day);

    /**
     * 更新tradeinfo结算状态
     * @param batchNo
     * @param code
     * @return
     */
    int updateTradeInfoStatus(Long batchNo, Integer code);
}
