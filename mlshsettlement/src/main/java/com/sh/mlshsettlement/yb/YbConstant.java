package com.sh.mlshsettlement.yb;

import com.sh.mlshcommon.util.StringUtil;

import java.util.Objects;

public class YbConstant {
    public enum TransferStatusEnum{
        s_0025("0025","已接收",true),s_0026("0026","已汇出",true),s_0027("0027","已退款",false),s_0028("0028","已拒绝",false),s_0029("0029","待复核",true),s_0030("0030","打款中",true);
        private String code;
        private String name;
        private boolean success;

        TransferStatusEnum(String code, String name,boolean success) {
            this.code = code;
            this.name = name;
            this.success = success;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public boolean isSuccess() {
            return success;
        }

        public static String getCodeByName(String code){
            for (TransferStatusEnum value : TransferStatusEnum.values()) {
                if(Objects.equals(value.code,code)){
                    return value.name;
                }
            }
            return "";
        }

        public static boolean isSuccess(String code){
            for (TransferStatusEnum value : TransferStatusEnum.values()) {
                if(StringUtil.equals(value.code,code)){
                    return value.success;
                }
            }
            return true;
        }
    }

    public enum BankTrxStatusCodeEnum{
        s_s("S","已成功","SUCCESS"),s_i("I","银行处理中","PROCESSING"),s_f("F","出款失败","FAIL"),s_w("W","未出款","PROCESSING"),s_u("U","未知","PROCESSING");
        private String code;
        private String name;
        private String success;

        private static String SUCCESS = "SUCCESS";
        private static String FAIL = "FAIL";
        private static String PROCESSING = "PROCESSING";

        BankTrxStatusCodeEnum(String code, String name,String success) {
            this.code = code;
            this.name = name;
            this.success = success;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public String getSuccess() {
            return success;
        }

        public static String getCodeByName(String code){
            for (BankTrxStatusCodeEnum value : BankTrxStatusCodeEnum.values()) {
                if(Objects.equals(value.code,code)){
                    return value.name;
                }
            }
            return "";
        }

        public static boolean isSuccess(String code){
            for (BankTrxStatusCodeEnum value : BankTrxStatusCodeEnum.values()) {
                if(StringUtil.equals(value.code,code) && StringUtil.equals(value.success,BankTrxStatusCodeEnum.SUCCESS)){
                    return true;
                }
            }
            return false;
        }
        public static boolean isProcessing(String code){
            for (BankTrxStatusCodeEnum value : BankTrxStatusCodeEnum.values()) {
                if(StringUtil.equals(value.code,code) && StringUtil.equals(value.success,BankTrxStatusCodeEnum.PROCESSING)){
                    return true;
                }
            }
            return false;
        }
    }
}
