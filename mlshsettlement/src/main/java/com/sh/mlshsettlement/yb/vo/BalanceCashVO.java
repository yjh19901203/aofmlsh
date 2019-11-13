package com.sh.mlshsettlement.yb.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceCashVO {
    /**
     * 商户编号
     **/
    private String customerNumber;

    /**
     * 系统商编号
     **/
    private String groupNumber;

    /**
     *  提现金额
     **/
    private BigDecimal amount;

    /**
     * 商户订单号
     **/
    private String orderId;

    /**
     * 	提现类型
     **/
    private String cashType;

    /**
     * 错误码，正常情况下不返回该项
     **/
    private String errorCode;
    /**
     * 错误信息，正常情况下不返回该项
     **/
    private String errorMsg;
}
