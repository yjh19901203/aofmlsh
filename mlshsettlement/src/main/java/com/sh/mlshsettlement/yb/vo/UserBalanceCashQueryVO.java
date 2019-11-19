package com.sh.mlshsettlement.yb.vo;

import com.alibaba.fastjson.JSONObject;
import com.yeepay.shade.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
public class UserBalanceCashQueryVO {
    /**
     *总数
     **/
    private int totalCount;

    /**
     *返回描述信息
     **/
    private String errorMsg;

    /**
     *	错误码
     **/
    private String errorCode;

    /**
     *	总页数
     **/
    private int totalPageSize;

    /**
     *每页显示条数
     **/
    private int pageSize;

    /**
     *	扩展信息
     **/
    private JSONObject extInfos;
    /**
     *	页码
     **/
    private int pageNo;
    /**
     * 返回list值
     */
    private List<UserBalanceCashQueryListVO> list;
}
