package com.sh.mlshsettlement.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class DepositVO {

    public static final String RESULT_SUCCESS = "SUCCESS";
    public static final String RESULT_FAIL = "FAIL";

    public DepositVO(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    /**
     * 响应结果码
     **/
    @ApiModelProperty(name = "返回码",value = "SUCCESS:成功，FAIL:失败")
    private String resultCode;

    /**
     * 提示信息
     **/
    @ApiModelProperty(name = "提示信息")
    private String resultMsg;

    public static DepositVO error(String resultMsg){
         return new DepositVO(DepositVO.RESULT_FAIL,resultMsg);

    }

    public static DepositVO success(){
        return new DepositVO(DepositVO.RESULT_SUCCESS,"提现申请成功");

    }
}
