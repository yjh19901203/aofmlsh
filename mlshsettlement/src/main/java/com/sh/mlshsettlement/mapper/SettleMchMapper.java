package com.sh.mlshsettlement.mapper;

import com.sh.mlshsettlement.model.SettleMch;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sh.mlshsettlement.vo.SummaryTradeInfoVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
public interface SettleMchMapper extends BaseMapper<SettleMch> {

    int batchInsertOrUpdate(List<SummaryTradeInfoVO> summaryTradeInfos);

    List<SettleMch> pageSelect(@Param("day") LocalDate day, @Param("id") Long id);

}
