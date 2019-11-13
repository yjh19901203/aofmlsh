package com.sh.mlshcommon.util;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

/**
 * ID生成器
 */
public class IdGenerator {

    public static String generateRandomString(int length) {
        StringBuilder baseStr = new StringBuilder("1");

        int base;
        for(base = 0; base < length - 1; ++base) {
            baseStr.append("0");
        }

        base = Integer.parseInt(baseStr.toString());
        int multiply = 9 * base;
        int result = (int)(Math.random() * (double)multiply) + base;
        return result +"";
    }

    public static String createPayFlowingId(String id){
        return DateUtil.dateToLong(new Date()) + IdGenerator.generateRandomString(5) + id;
    }


    public static String generatorId(String sign,int length){

        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {//有可能是负数
            hashCodeV = -hashCodeV;
        }
        String flowingId=sign + String.format("%0"+(length-sign.length())+"d", hashCodeV);
        return flowingId;
    }

    public static long generatorIdStr(LocalDate date){

        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {//有可能是负数
            hashCodeV = -hashCodeV;
        }
        String localDate = "";
        if(date!=null){
            localDate = DateUtil.formatLocalDate(date, DateUtil.YYYYMMDD2);
        }

        String flowingId=localDate + hashCodeV;
        return Long.parseLong(flowingId);
    }

}
