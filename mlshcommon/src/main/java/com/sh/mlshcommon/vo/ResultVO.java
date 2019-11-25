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
    private static final int system_error = 9999;
    private static final int exception = -9999;
    private static final int processing = 0;

    @ApiModelProperty(value = "返回码，1：成功  -1：失败")
    private int code;
    @ApiModelProperty(value = "返回的信息")
    private String msg;
    @ApiModelProperty(value = "返回的数据")
    private T data;

    public ResultVO(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ResultVO success(T data){
        return new ResultVO(success,"成功",data);
    }

    public static ResultVO success(){
        return new ResultVO(success,"成功",null);
    }

    public static ResultVO error(String failMsg){
        return new ResultVO(fail,failMsg,null);
    }

    public static ResultVO error(int code,String failMsg){
        return new ResultVO(code,failMsg,null);
    }

    public static ResultVO systemError(String failMsg){
        return new ResultVO(system_error,failMsg,null);
    }

    public static ResultVO exceptionError(String failMsg){
        return new ResultVO(exception,failMsg,null);
    }

    public static ResultVO processing(String msg){
        return new ResultVO(processing,msg,null);
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
    public boolean isFail(){
        if(code == fail){
            return true;
        }
        return false;
    }
    public boolean isProcessing(){
        if(code == processing){
            return true;
        }
        return false;
    }
}
