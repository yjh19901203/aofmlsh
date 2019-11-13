package com.sh.mlshcommon.util;

import com.alibaba.fastjson.JSONObject;

public class JSONUtil {

    public static  <T> T toObject(JSONObject obj,Class<T> cla){
        return JSONObject.toJavaObject(obj,cla);
    }
}
