package com.sh.mlshapi.controller.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@ApiModel
@Data
public class DepositPO {
    /**
     * 用户id
     **/
    @ApiModelProperty(value = "用户ID",required = true)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 请求流水号
     **/
    @ApiModelProperty(value = "请求流水号",required = true)
    @NotNull(message = "请求流水号不能为空")
    private Long transactionId;

    /**
     * 金额
     **/
    @ApiModelProperty(value = "金额",required = true)
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    /**
     * 账户名
     **/
    @ApiModelProperty(value = "账户名",required = true)
    @NotEmpty(message = "账户名不能为空")
    private String accountName;

    /**
     * 卡号
     **/
    @ApiModelProperty(value = "卡号",required = true)
    @NotEmpty(message = "卡号为空")
    private String accountNumber;

    /**
     * 银行编码
     **/
    @ApiModelProperty(value = "银行编码",required = true)
    @NotEmpty(message = "银行编码为空")
    private String bankCode;
    /**
     * 支行名称
     **/
    @ApiModelProperty(value = "支行名称",required = false)
    String bankBranchName;
    /**
     * 省
     **/
    @ApiModelProperty(value = "省",required = false)
    String provinceCode;
    /**
     * 市
     **/
    @ApiModelProperty(value = "市",required = false)
    String cityCode;
    /**
     * 通知url
     **/
    @ApiModelProperty(value = "通知url",required = true)
    @NotEmpty(message = "通知url为空")
    String notifyUrl;

}
