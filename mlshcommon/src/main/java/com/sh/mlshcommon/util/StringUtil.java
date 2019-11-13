package com.sh.mlshcommon.util;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

public class StringUtil {

    public static boolean isEmpty(String str){
        return StringUtils.isEmpty(str);
    }

    public static boolean isNull(LocalDate localDate) {
        return localDate==null;
    }
}
