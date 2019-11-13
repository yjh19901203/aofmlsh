package com.sh.mlshsettlement.vo;

import com.sh.mlshcommon.util.IdGenerator;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class SummaryTradeInfoVO implements Serializable {
    /**
     * 易宝商编
     **/
    private String ybMchId;
    /**
     * 商户id
     **/
    private Long mchId;
    /**
     * 主商户id
     **/
    private Long parentMchId;

    /**
     * 商户名称
     **/
    private String mchName;

    /**
     * 汇总日期
     **/
    private LocalDate summaryDay;

    /**
     * 结算金额
     **/
    private BigDecimal settleAmount;

    /**
     * 结算手续费
     **/
    private BigDecimal settleFee;

    /**
     * 结算状态
     **/
    private Integer settleStatus;

    /**
     * tradeinfo 主键
     */
    private String tradeInfoId;
    /**
     * tradeinfo最大ID
     */
    private Long maxId;
    /**
     * batchNo
     */
    private Long batchNo;
    /**
     * 订单数
     */
    private int orderNum;

    public Long getBatchNo() {
        return IdGenerator.generatorIdStr(getSummaryDay());
    }
}
