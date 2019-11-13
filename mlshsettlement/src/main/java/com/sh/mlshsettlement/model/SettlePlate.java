package com.sh.mlshsettlement.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author jinghui.yu
 * @since 2019-11-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SettlePlate implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 易宝商编
     */
    @TableField("yb_mch_id")
    private String ybMchId;

    @TableField("mch_id")
    private Long mchId;

    @TableField("mch_name")
    private String mchName;

    /**
     * 汇总结算日期
     */
    @TableField("summary_day")
    private Date summaryDay;

    /**
     * 结算金额
     */
    @TableField("settle_amount")
    private BigDecimal settleAmount;

    /**
     * 结算手续费
     */
    @TableField("settle_fee")
    private BigDecimal settleFee;

    /**
     * 结算状态  1：未结算  2：已结算
     */
    @TableField("settle_status")
    private Integer settleStatus;

    /**
     * 支付状态  1：支付中  3：支付成功   2：支付失败
     */
    @TableField("pay_status")
    private Integer payStatus;

    /**
     * 结算时间
     */
    @TableField("pay_time")
    private Date payTime;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;


}
