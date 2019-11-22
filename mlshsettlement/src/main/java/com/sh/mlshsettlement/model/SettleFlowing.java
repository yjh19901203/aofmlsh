package com.sh.mlshsettlement.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

import io.swagger.models.auth.In;
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
public class SettleFlowing implements Serializable {

    private static final long serialVersionUID=1L;

    public enum SettleSourceEnum{
        s_1(1,"商户结算"),s_2(2,"用户结算"),s_3(3,"平台");

        private Integer code;
        private String name;

        SettleSourceEnum(Integer code, String name) {
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

    public enum NotifyStatusEnum{
        s_1(1,"未通知"),s_2(2,"通知成功"),s_3(3,"通知失败");

        private Integer code;
        private String name;

        NotifyStatusEnum(Integer code, String name) {
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

    public enum FlowingSettleStatusEnum{
        s_1(1,"结算中"),s_2(2,"结算成功"),s_3(3,"结算失败");

        private Integer code;
        private String name;

        FlowingSettleStatusEnum(Integer code, String name) {
            this.code = code;
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public static String getNameByCode(Integer code){
            for (FlowingSettleStatusEnum value : FlowingSettleStatusEnum.values()) {
                if(Objects.equals(code,value.code)){
                    return value.name;
                }
            }
            return "";
        }
    }

    public enum MlSettleStatusEnum{
        s_1(1,"","结算中"),s_2(2,"REMITTANCE_SUCCESS","结算成功"),s_3(3,"REMITTANCE_FAIL","结算失败");

        ;
        private Integer code;
        private String mlCode;
        private String name;

        MlSettleStatusEnum(Integer code,String mlCode, String name) {
            this.code = code;
            this.mlCode = mlCode;
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public String getMlCode() {
            return mlCode;
        }

        public static String getMlCodeByCode(Integer code){
            for (MlSettleStatusEnum value : MlSettleStatusEnum.values()) {
                if(Objects.equals(code,value.code)){
                    return value.mlCode;
                }
            }
            return "";
        }
    }

    public SettleFlowing() {
    }

    public SettleFlowing(String settleFlowing, Integer settleSource, Long settleSign, String settleMch, BigDecimal settleAmount, String notifyUrl, Integer notifyStatus) {
        this.settleFlowing = settleFlowing;
        this.settleSource = settleSource;
        this.settleSign = settleSign;
        this.settleMch = settleMch;
        this.settleAmount = settleAmount;
        this.notifyUrl = notifyUrl;
        this.notifyStatus = notifyStatus;
    }

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 结算流水
     */
    @TableField("settle_flowing")
    private String settleFlowing;

    /**
     * 结算来源  1：商户结算  2：用户结算  3：平台
     */
    @TableField("settle_source")
    private Integer settleSource;

    /**
     * 结算标识
     */
    @TableField("settle_sign")
    private Long settleSign;

    /**
     * 结算商编
     */
    @TableField("settle_mch")
    private String settleMch;

    /**
     * 结算金额
     */
    @TableField("settle_amount")
    private BigDecimal settleAmount;
    /**
     * 实际结算金额金额
     */
    @TableField("real_pay_amount")
    private BigDecimal realPayAmount;

    /**
     * 结算状态  1：发起  2：结算成功  3：结算失败
     */
    @TableField("settle_status")
    private Integer settleStatus;
    /**
     * 结算失败原因
     */
    @TableField("settle_desc")
    private String settleDesc;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableField("settle_account_no")
    private String settleAccountNo;

    @TableField("settle_account_name")
    private String settleAccountName;

    @TableField("notify_url")
    private String notifyUrl;

    @TableField("pay_time")
    private LocalDateTime payTime;

    @TableField("notify_status")
    private Integer notifyStatus;

    @TableField("notify_time")
    private LocalDateTime notifyTime;

}
