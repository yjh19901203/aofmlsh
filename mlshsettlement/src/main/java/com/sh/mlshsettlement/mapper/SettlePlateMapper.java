package com.sh.mlshsettlement.mapper;

import com.sh.mlshsettlement.model.SettlePlate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sh.mlshsettlement.vo.SummaryPlateVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jinghui.yu
 * @since 2019-11-05
 */
@Repository
public interface SettlePlateMapper extends BaseMapper<SettlePlate> {

    int batchInsertOrUpdate(List<SummaryPlateVO> summaryPlateVOS);
}
