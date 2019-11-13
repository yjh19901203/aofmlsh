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
public class SettleMch implements Serializable {

    private static final long serialVersionUID=1L;

    public enum SettleStatusEnum{
        s_1(1,"未结算"),s_2(2,"已结算");

        private Integer code;
        private String name;

        SettleStatusEnum(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

    public enum PayStatusEnum{
        p_1(1,"支付中"),p_2(2,"支付失败"),p_3(3,"支付成功"),p_0(0,"未支付");

        private Integer code;
        private String name;

        PayStatusEnum(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

    public SettleMch() {
    }

    public SettleMch(Long id, Integer payStatus, Date payTime) {
        this.id = id;
        this.payStatus = payStatus;
        this.payTime = payTime;
    }

    public SettleMch(Long id,BigDecimal payRealAmount, Integer settleStatus, Integer payStatus, Date payTime, String payDesc, String payBankNo, String payBankName) {
        this.id = id;
        this.payRealAmount = payRealAmount;
        this.settleStatus = settleStatus;
        this.payStatus = payStatus;
        this.payTime = payTime;
        this.payDesc = payDesc;
        this.payBankNo = payBankNo;
        this.payBankName = payBankName;
    }

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 结算批次号
     */
    @TableField("batch_no")
    private Long batchNo;

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
    private LocalDate summaryDay;

    /**
     * 结算金额
     */
    @TableField("settle_amount")
    private BigDecimal settleAmount;
    /**
     * 实际结算金额
     */
    @TableField("pay_real_amount")
    private BigDecimal payRealAmount;

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
     * 支付状态  1：支付中  2：支付失败  3：支付成功
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

    @TableField("pay_desc")
    private String payDesc;
    @TableField("pay_bank_no")
    private String payBankNo;
    @TableField("pay_bank_name")
    private String payBankName;


}
