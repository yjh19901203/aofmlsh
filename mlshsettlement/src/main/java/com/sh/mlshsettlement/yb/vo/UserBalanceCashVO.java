package com.sh.mlshsettlement.yb.vo;

import lombok.Data;

@Data
public class UserBalanceCashVO {
    /**
     *返回描述信息
     **/
    private String errorMsg;

    /**
     *	错误码
     **/
    private String errorCode;
    /**
     * 出款订单号
     **/
    private String orderId;

    /**
     * 出款批次号
     **/
    private String batchNo;

    /**
     * 打款状态码
     **/
    private String transferStatusCode;

    /**
     * 是否实时到账
     **/
    private Boolean urgency;

}
