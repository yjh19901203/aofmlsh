package com.sh.mlshcommon.util;

import com.alibaba.fastjson.JSONObject;

public class JSONUtil {

    public static  <T> T toObject(JSONObject obj,Class<T> cla){
        return JSONObject.toJavaObject(obj,cla);
    }

    public static  <T> T parseObject(String str,Class<T> cla){
        return JSONObject.parseObject(str,cla);
    }

    public static  JSONObject toJsonObject(String jsonStr){
        return JSONObject.parseObject(jsonStr);
    }

    public static String toString(Object obj) {
        return JSONObject.toJSONString(obj);
    }
}
