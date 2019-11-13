package com.sh.mlshsettlement.common;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.java.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
@Log
@Data
public class LogModel {
    private Map<String, Object> datas;
    private String method;
    private final AtomicInteger serialId = new AtomicInteger(0);
    private final long startTime;
    private LogModel(String name) {
        datas = new HashMap<String, Object>();
        startTime=System.currentTimeMillis();
        method=name+"#"+startTime+"#";
        datas.put("_method", method);
    }
    public String getName(){
        return this.method;
    }
    public static LogModel newLogModel(String method) {
        return new LogModel(method);
    }

    public LogModel setResultMessage(long result, String message) {
        addData("_result", result).addData("_message", message);
        return this;
    }

    public LogModel addStart(Object value) {
        if (value != null)
            datas.put("start", value);
        else
            datas.put("start", "");
        return this;
    }

    public LogModel addEnd(Object value) {
        if (value != null)
            datas.put("end", value);
        else
            datas.put("end", "");
        return this;
    }

    public LogModel addException(Object value) {
        if (value != null)
            datas.put("exception", value);
        else
            datas.put("exception", "");
        return this;
    }

    public LogModel addReq(Object value) {
        if (value != null)
            datas.put("req", value);
        else
            datas.put("req", "");
        return this;
    }

    public LogModel addRep(Object value) {
        if (value != null)
            datas.put("rep", value);
        else
            datas.put("rep", "");
        return this;
    }

    public LogModel addData(String key, Object value) {
        if (value != null)
            datas.put(key, value);
        else
            datas.put(key, "");
        return this;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : datas.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    private void purge(){
        this.datas.clear();
        datas.put("_method", method);
    }
    public String toJson() {
        return toJson(true);
    }

    private void addTimeSlow(long cost){
        if(cost>2000)
            datas.put("_costType",">2000");
        else if(cost>1000)
            datas.put("_costType",">1000");
        else if(cost>500)
            datas.put("_costType",">500");
        else if(cost>200)
            datas.put("_costType",">200");
        else if(cost>100)
            datas.put("_costType",">100");
        else
            datas.put("_costType","<100");
    }

    public String toJson(boolean purge) {
        try {
            datas.put("_serialId", serialId.incrementAndGet());
            long cost = System.currentTimeMillis()-startTime;
            datas.put("_LogCost", cost);
            addTimeSlow(cost);
            if(purge){
                String str= JSONObject.toJSONString(datas);
                purge();
                return str;
            }else{
                return JSONObject.toJSONString(toMap());
            }
        } catch(Exception e){
            e.printStackTrace();
            return "{data:error}";
        }
    }
}
