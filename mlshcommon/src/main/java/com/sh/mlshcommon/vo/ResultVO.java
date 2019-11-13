package com.sh.mlshcommon.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiResponse;
import lombok.Data;

import java.io.Serializable;

@ApiModel
@Data
public class ResultVO<T> implements Serializable {
    private static final int success = 1;
    private static final int fail = -1;

    @ApiModelProperty(value = "返回码，1：成功  -1：失败")
    private int code;
    @ApiModelProperty(value = "返回的信息")
    private String msg;
    @ApiModelProperty(value = "返回的数据")
    private T data;
    @ApiModelProperty(value = "业务返回码")
    private int businessCode;
    @ApiModelProperty(value = "业务返回信息")
    private String businessMsg;

    public ResultVO(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResultVO(int code, String msg, T data, int businessCode,String businessMsg) {
        this.code = code;
        this.msg = msg;
        this.businessMsg = businessMsg;
        this.data = data;
        this.businessCode = businessCode;
    }

    public static <T> ResultVO success(T data){
        return new ResultVO(success,"成功",data);
    }

    public static ResultVO success(){
        return new ResultVO(success,"成功",null);
    }

    public static <T> ResultVO success(int businessCode,String businessMsg,T data){
        return new ResultVO(success,"成功",null,businessCode,businessMsg);
    }

    public static ResultVO error(String failMsg){
        return new ResultVO(fail,failMsg,null);
    }

    public T getData() {
        return data;
    }

    public boolean isSuccess(){
        if(code == success){
            return true;
        }
        return false;
    }
}
