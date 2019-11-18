package com.sh.mlshsettlement.yb.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UserBalanceCashQueryListVO {
    /**
     *订单号
     **/
    private String orderId;

    /**
     *批次号
     **/
    private String batchNo;

    /**
     *账户名
     **/
    private String accountName;

    /**
     *账户号
     **/
    private String accountNumber;

    /**
     *出款金额
     **/
    private BigDecimal amount;

    /**
     *银行支行名称
     **/
    private String bankBranchName;
    /**
     *银行编码
     **/
    private String bankCode;

    /**
     *银行名称
     **/
    private String bankName;

    /**
     *银行状态码
     **/
    private String bankTrxStatusCode;

    /**
     *城市编码
     **/
    private String cityCode;

    /**
     *手续费
     **/
    private BigDecimal fee;

    /**
     *手续费类型
     **/
    private String feeType;
    /**
     *	留言
     **/
    private String leaveWord;

    /**
     *省编码
     **/
    private String provinceCode;

    /**
     *打款状态码
     **/
    private String transferStatusCode;

    /**
     *是否加急
     **/
    private String urgency;

    /**
     *	加急类型
     **/
    private String urgencyType;

    /**
     *成功金额
     **/
    private String successAmount;
    /**
     *失败金额
     **/
    private String failAmount;

    /**
     *退款金额
     **/
    private String refundAmount;

    /**
     *完成时间
     **/
    private String finishDate;

    /**
     *	银行冲退状态
     **/
    private String bankRetreatStatus;

    /**
     *银行错误信息
     **/
    private String bankMsg;

}
