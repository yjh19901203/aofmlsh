package com.sh.mlshapi.controller.po;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class DepositPO {
    /**
     * 用户id
     **/
    private Long userId;

    /**
     * 订单号
     **/
    @NotNull(message = "请求流水号不能为空")
    private Long transactionId;

    /**
     * 易宝商编
     **/
    @NotEmpty(message = "易宝商编不能为空")
    private String ybMerchantNo;

    /**
     * 金额
     **/
    @NotNull(message = "提现金额不能为空")
    @DecimalMin(value = "0.01",message = "提现金额最小值0.01")
    private BigDecimal amount;

}
