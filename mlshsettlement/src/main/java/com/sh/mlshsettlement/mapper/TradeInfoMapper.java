package com.sh.mlshsettlement.mapper;

import com.sh.mlshsettlement.model.TradeInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sh.mlshsettlement.vo.SummaryPlateVO;
import com.sh.mlshsettlement.vo.SummaryTradeInfoVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 商户提现明细表 Mapper 接口
 * </p>
 *
 * @author jinghui.yu
 * @since 2019-11-05
 */
@Repository
public interface TradeInfoMapper extends BaseMapper<TradeInfo> {

    List<SummaryTradeInfoVO> summaryTradeInfo(@Param("day") LocalDate day,@Param("id") Long id);

    List<SummaryPlateVO> summaryPlateTradeInfo(LocalDate day);
}
