package com.sh.mlshsettlement.yb.vo;

import com.sh.mlshcommon.util.StringUtil;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

@Data
public class BalanceCashQueryVO {

    public enum YbPayStatus {
        s_24("0024", 1,"已挂起"), s_25("0025", 1,"已接收"),
        s_26("0026", 1,"已汇出"),s_27("0027", 3,"已退款"),
        s_28("0028", 3,"已拒绝"),s_29("0029", 3,"待复核"),
        s_30("0030", 3,"结算失败");

        private String code;
        private Integer shCode;
        private String name;

        YbPayStatus(String code, Integer shCode,String name) {
            this.code = code;
            this.shCode = shCode;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public Integer getShCode() {
            return shCode;
        }

        public String getName() {
            return name;
        }
    }

    public enum YlPayStatus {
        s_s("S", 2,"成功"), s_i("I", 1,"银联处理中"),
        s_f("F", 3,"失败"),s_w("W", 1,"未出款"),
        s_u("U", 1,"未知");

        private String code;
        private Integer shCode;
        private String name;

        YlPayStatus(String code, Integer shCode,String name) {
            this.code = code;
            this.shCode = shCode;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public Integer getShCode() {
            return shCode;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 提现卡号
     **/
    private String bankAccountNo;

    /**
     * 提现金额，单位元，精确到小数点后2位
     **/
    private BigDecimal amount;

    /**
     * 手续费，单位元，精确到小数点后2位
     **/
    private BigDecimal fee;

    /**
     * 提现账户名称
     **/
    private String bankAccountName;

    /**
     * 商户编号
     **/
    private String customerNumber;

    /**
     * 错误码
     **/
    private String errorCode;
    /**
     * 错误信息
     **/
    private String errorMsg;
    /**
     * 提现类型
     **/
    private String cashType;
    /**
     * 商户订单号
     **/
    private String orderId;
    /**
     * 银行状态
     * <p>
     * 枚举：
     * <p>
     * S:已成功
     * I:银行处理中
     * F:出款失败
     * W:未出款
     * U:未知
     * S与F为最终状态，其余全部是中间状态；订单处于中间状态时，不要补发本次出款，避免资金损失
     **/
    private String bankTrxStatusCode;
    /**
     * 结束时间
     **/
    private String finishTime;
    /**
     * 退款时间
     **/
    private String refundTime;
    /**
     * 银行返回错误码，只有出错时才有
     **/
    private String bankMsg;
    /**
     * 提现支行名称
     **/
    private String branchName;
    /**
     * 实际提现额度
     **/
    private BigDecimal realAmount;
    /**
     * 易宝出款状态
     * <p>
     * 枚举
     * <p>
     * 0024：打款结果：已挂起
     * 0025：打款结果：已接收
     * 0026：打款结果：已汇出
     * 0027：打款结果：已退款
     * 0028：打款结果：已拒绝
     * 0029：打款结果：待复核
     * 0030：打款结果：未知
     **/
    private String transferStatusCode;
    /**
     * 计费类型
     * SOURCE：付款方手续费
     * TARGET：收款方手续费
     **/
    private String feeType;
    /**
     * 系统商编号
     **/
    private String groupNumber;
    /**
     * 提现银行
     **/
    private String bankName;


    public static Integer getSettleStatus(String ybStatus,String ylStatus){
        if(StringUtil.isEmpty(ybStatus) || Objects.equals(ybStatus,YbPayStatus.s_26.code)){
            for (YlPayStatus value : YlPayStatus.values()) {
                if(Objects.equals(ylStatus,value.code)){
                    return value.shCode;
                }
            }
            return null;
        }
        for (YbPayStatus value : YbPayStatus.values()) {
            if(Objects.equals(ybStatus,value.code)){
                return value.shCode;
            }
        }
        return null;
    }
}
