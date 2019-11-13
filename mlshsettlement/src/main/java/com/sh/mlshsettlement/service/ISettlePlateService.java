package com.sh.mlshsettlement.service;

import com.sh.mlshsettlement.model.SettlePlate;
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
public interface ISettlePlateService extends IService<SettlePlate> {
    void summaryPlateSettle(LocalDate day);
}
