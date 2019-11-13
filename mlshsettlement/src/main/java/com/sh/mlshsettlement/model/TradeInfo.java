package com.sh.mlshsettlement.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import io.swagger.models.auth.In;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商户提现明细表
 * </p>
 *
 * @author jinghui.yu
 * @since 2019-11-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TradeInfo implements Serializable {

    private static final long serialVersionUID=1L;

    public enum StatusEnum{
        s_1(1,"未提现"),s_2(2,"处理中"),s_3(3,"提现成功"),s_4(4,"提现失败");

        private Integer code;
        private String name;

        StatusEnum(Integer code, String name) {
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

    public enum SettleStatusEnum{
        s_1(0,1,"未支付"),s_2(1,2,"处理中"),s_3(2,3,"提现成功"),s_4(3,4,"提现失败");

        private Integer code;
        private Integer tradeCode;
        private String name;

        SettleStatusEnum(Integer code,Integer tradeCode, String name) {
            this.tradeCode = tradeCode;
            this.code = code;
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public Integer getTradeCode() {
            return tradeCode;
        }

        public String getName() {
            return name;
        }

        public static Integer getTradeCodeByCode(Integer code){
            for (SettleStatusEnum value : SettleStatusEnum.values()) {
                if(Objects.equals(code,value.getCode())){
                    return value.getTradeCode();
                }
            }
            return null;
        }
    }

    public TradeInfo() {
    }

    public TradeInfo(Long batchNo, Integer status) {
        this.batchNo = batchNo;
        this.status = status;
    }

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 交易标题
     */
    @TableField("title")
    private String title;

    /**
     * 交易内容
     */
    @TableField("content")
    private String content;

    /**
     * 加密金额
     */
    @TableField("encryption_amount")
    private String encryptionAmount;

    /**
     * 金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 本表关联id
     */
    @TableField("self_id")
    private Integer selfId;

    /**
     * order_tail表id
     */
    @TableField("ref_id")
    private Integer refId;

    /**
     * 关联类型 
1 团购(团购id , product_info) 
2 店铺（订单id,order_info）
3 电商
     */
    @TableField("ref_type")
    private Integer refType;

    /**
     * 退货状态 0待审核  1待退款  2仅退款成功   3退货成功  4退货退款成功    5拒绝退货''  6拒绝退款 7强制退货成功 8强制退款9平台拒绝退款10平台拒绝退货 
     */
    @TableField("refund_status")
    private Integer refundStatus;

    /**
     * 提现批次
     */
    @TableField("batch_no")
    private Long batchNo;

    /**
     * 审核内容（转款失败内容）
     */
    @TableField("audit_content")
    private String auditContent;

    /**
     * 商户id
     */
    @TableField("merchant_id")
    private Integer merchantId;

    /**
     * 结算商户id
     */
    @TableField("sub_merchant_id")
    private Integer subMerchantId;

    /**
     * 申请提现时间
     */
    @TableField("application_time")
    private Date applicationTime;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 订单编号-作废
     */
    @TableField("code")
    private String code;

    /**
     * 销售总金额-作废
     */
    @TableField("total_pay")
    private BigDecimal totalPay;

    /**
     * 消费人数-作废
     */
    @TableField("total_num")
    private Integer totalNum;

    /**
     * 特殊商品金额-作废
     */
    @TableField("special_pay")
    private BigDecimal specialPay;

    /**
     * 提现用户
     */
    @TableField("openid")
    private String openid;

    /**
     * 微信手续费
     */
    @TableField("wx_rate")
    private BigDecimal wxRate;

    /**
     * 微信收款人真实姓名
     */
    @TableField("wx_user_name")
    private String wxUserName;

    /**
     * 交易号
     */
    @TableField("wx_partner_trade_no")
    private String wxPartnerTradeNo;

    /**
     * 微信返回结果
     */
    @TableField("wx_result_msg")
    private String wxResultMsg;

    /**
     * 转款尝试次数
     */
    @TableField("num")
    private Integer num;

    /**
     * 微信付款成功时间
     */
    @TableField("wx_payment_time")
    private String wxPaymentTime;

    /**
     * 备注信息
     */
    @TableField("wx_desc")
    private String wxDesc;

    /**
     * 交易状态 1 未提现 2 处理中 3提现成功 4.提现失败
     */
    @TableField("status")
    private Integer status;

    /**
     * 类型 
1 入账(客户消费产生的可提现金额)
2提现（商户佣金产生的可提现金额） 
3转款（实际指的是财务向公司指定人员转款）
4退款（用户退款）
     */
    @TableField("type")
    private Integer type;

    /**
     * 此笔交易是否确认0未确认 1已确认 -1已退款
     */
    @TableField("confirm")
    private Integer confirm;

    /**
     * 商户易宝商编
     */
    @TableField("yb_merchant_no")
    private String ybMerchantNo;

    /**
     * 商户结算时间（根据核销时间d+1还是d+8）
     */
    @TableField("jiesuan_time")
    private Date jiesuanTime;

    /**
     * 平台分润金额
（商品现价-商品结算金额-上家佣金-上上家佣金）
     */
    @TableField("plat_amt")
    private BigDecimal platAmt;

    /**
     * 平台易宝商编
     */
    @TableField("plat_yb_merchant_no")
    private String platYbMerchantNo;

    /**
     * 结算金额
     */
    @TableField("jiesuan_amt")
    private BigDecimal jiesuanAmt;
    /**
     * 结算订单笔数
     */
    @TableField(exist = false)
    private int orderNum;
}
