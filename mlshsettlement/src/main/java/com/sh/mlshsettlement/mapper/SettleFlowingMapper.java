package com.sh.mlshsettlement.mapper;

import com.sh.mlshsettlement.model.SettleFlowing;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
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
public interface SettleFlowingMapper extends BaseMapper<SettleFlowing> {

    List<SettleFlowing> pageList(@Param("id") Long id,@Param("source") Integer source,@Param("sign") Long sign);
}
