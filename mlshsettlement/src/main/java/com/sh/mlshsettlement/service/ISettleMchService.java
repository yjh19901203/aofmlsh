package com.sh.mlshsettlement.service;

import com.sh.mlshsettlement.model.SettleMch;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jinghui.yu
 * @since 2019-11-05
 */
public interface ISettleMchService extends IService<SettleMch> {
    /**
     * 商户结算单汇总
     * @param day
     */
    void summaryMchSettle(LocalDate day);

    /**
     * 商户结算
     * @param day
     */
    void settleMch(LocalDate day);

}
