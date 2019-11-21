package com.sh.mlshsettlement.service;

import com.sh.mlshcommon.vo.ResultVO;
import com.sh.mlshsettlement.model.SettleFlowing;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jinghui.yu
 * @since 2019-11-05
 */
public interface ISettleFlowingService extends IService<SettleFlowing> {
    String insertFlowing(Integer settleSource, Long settleSign, String settleMch, BigDecimal settleAmount,String notifyUrl);

    void queryFlowingResultAll(Integer source,Long sign);

    void updateFlowingFail(String flowing, String msg);

    SettleFlowing queryFlowingResult(Integer settleSource,Long settleSign);

    ResultVO userDeposit(Long requestNo, String accountName, BigDecimal amount,String accountNumber,String bankCode,Long userId,String bankBranchName,String provinceCode,String cityCode,String notifyUrl);

    /**
     * 用户通知接口
     */
    void userNotify();
}
