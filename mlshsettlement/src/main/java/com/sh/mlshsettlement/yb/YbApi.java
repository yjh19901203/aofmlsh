package com.sh.mlshsettlement.yb;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.sh.mlshcommon.util.JSONUtil;
import com.sh.mlshcommon.util.StringUtil;
import com.sh.mlshcommon.vo.ResultVO;
import com.sh.mlshsettlement.common.LogModel;
import com.sh.mlshsettlement.common.logreqaspect.LogRequestAnnotion;
import com.sh.mlshsettlement.yb.vo.BalanceCashQueryVO;
import com.sh.mlshsettlement.yb.vo.BalanceCashVO;
import com.sh.mlshsettlement.yb.vo.UserBalanceCashQueryVO;
import com.yeepay.g3.sdk.yop.client.YopClient3;
import com.yeepay.g3.sdk.yop.client.YopRequest;
import com.yeepay.g3.sdk.yop.client.YopResponse;
import com.yeepay.g3.sdk.yop.error.YopError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;

@Slf4j
@Component
public class YbApi {

    private String BalanceCashUrl = "/rest/v1.0/balance/cash";
    private String BalanceCashQueryUrl = "/rest/v1.0/balance/query-cash-byorder";

    private String UserBalanceCashUrl = "/rest/v1.0/balance/transfer_send";
    private String UserBalanceCashQueryUrl = "/rest/v1.0/balance/transfer_query";

    @Resource
    private YbConfig ybConfig;

    /**
     * 提现
     **/
    @LogRequestAnnotion(type = 1)
    public ResultVO balanceCash(String merchantId, String orderId, String cashType, BigDecimal amount){
        LogModel lm = LogModel.newLogModel("balanceCash").addStart(String.format("merchantId:%s,orderId:%s,cashType:%s,amount:%s", merchantId, orderId, cashType, amount));
        try{
            if(StringUtil.isEmpty(cashType)){
                cashType = "D1";
            }
            String payAmount = new DecimalFormat("0.00").format(amount);
            YopRequest request = new YopRequest(ybConfig.getAppkey(), ybConfig.getShprivatekey());
            request.addParam("customerNumber",merchantId);
            request.addParam("amount",payAmount);
            request.addParam("orderId",orderId);
            request.addParam("cashType",cashType);
            lm.addReq(request.toQueryString());
            YopResponse yopResponse = YopClient3.postRsa(BalanceCashUrl, request);
            lm.addRep(yopResponse.toString());
            String state = yopResponse.getState();
            if(Objects.equals(state,"FAILURE")){
                return ResultVO.error(yopResponse.getError().getMessage());
            }
            BalanceCashVO balanceCashVO = yopResponse.unmarshal(BalanceCashVO.class);
            if(StringUtil.isEmpty(balanceCashVO.getErrorCode())){
                return ResultVO.success();
            }
            return ResultVO.error(balanceCashVO.getErrorMsg());
        }catch(Exception e){
            lm.addException(e.getMessage());
            log.error("请求易宝提现异常",e);
            return ResultVO.error("请求易宝提现异常");
        }finally {
            log.info(lm.toJson());
        }
    }

    /**
     * 用户提现
     **/
    @LogRequestAnnotion(type = 2)
    public ResultVO userDeposit(String requestNo, String accountName, BigDecimal amount,String accountNumber,String bankCode,String bankBranchName,String provinceCode,String cityCode){
        LogModel lm = LogModel.newLogModel("userDeposit").addStart(String.format("requestNo:%s,accountName:%s,amount:%s,accountNumber:%s,bankCode:%s", requestNo, accountName, amount,accountNumber,bankCode));
        try{
            String payAmount = new DecimalFormat("0.00").format(amount);
            YopRequest request = new YopRequest(ybConfig.getAppkey(), ybConfig.getShprivatekey());
            request.addParam("batchNo",requestNo);
            request.addParam("orderId",requestNo);
            request.addParam("amount",payAmount);
            request.addParam("accountName",accountName);
            request.addParam("accountNumber",accountNumber);
            request.addParam("bankCode",bankCode);
            request.addParam("bankBranchName",bankBranchName);
            request.addParam("provinceCode",provinceCode);
            request.addParam("cityCode",cityCode);
            lm.addReq(request.toQueryString());
            YopResponse yopResponse = YopClient3.postRsa(UserBalanceCashUrl, request);
            lm.addRep(yopResponse.toString());
            String state = yopResponse.getState();
            if(!Objects.equals(state,"SUCCESS")){
                YopError error = yopResponse.getError();
                return ResultVO.error("易宝侧："+error.getCode()+"&&"+error.getMessage());
            }
            JSONObject jsonObject = JSONUtil.toJsonObject(yopResponse.getStringResult());
            String tradeStatus = jsonObject.getString("tradeStatus");
            if(!Objects.equals(tradeStatus,"REMITING")){
                return ResultVO.error(jsonObject.getString("message"));
            }
            return ResultVO.success(jsonObject.getString("businessNo"));
        }catch(Exception e){
            lm.addException(e.getMessage());
            log.error("请求易宝用户提现异常",e);
            return ResultVO.error("请求易宝用户提现异常");
        }finally {
            log.info(lm.toJson());
        }
    }

    /**
     * 商户提现查询
     **/
    public ResultVO<BalanceCashQueryVO> balanceCashQuery(String merchantId, String orderId, String cashType){
        LogModel lm = LogModel.newLogModel("balanceCashQuery").addStart(String.format("merchantId:%s,orderId:%s,cashType:%s", merchantId, orderId, cashType));
        try{
            YopRequest request = new YopRequest(ybConfig.getAppkey(), ybConfig.getShprivatekey());
            request.addParam("customerNumber",merchantId);
            request.addParam("orderId",orderId);
            request.addParam("cashType",cashType);
            lm.addReq(request.toQueryString());
            YopResponse yopResponse = YopClient3.postRsa(BalanceCashQueryUrl, request);
            lm.addRep(yopResponse.toString());
            String state = yopResponse.getState();
            if(Objects.equals(state,"FAILURE")){
                return ResultVO.error(yopResponse.getError().getMessage());
            }
            BalanceCashQueryVO balanceCashQueryVO = yopResponse.unmarshal(BalanceCashQueryVO.class);
            String errorCode = balanceCashQueryVO.getErrorCode();
            if(!StringUtil.isEmpty(errorCode)){
                return ResultVO.error(balanceCashQueryVO.getErrorMsg());
            }
            return ResultVO.<BalanceCashQueryVO>success(balanceCashQueryVO);
        }catch(Exception e){
            lm.addException(e.getMessage());
            log.error("请求易宝提现异常",e);
            return ResultVO.error("请求易宝提现异常");
        }finally {
            log.info(lm.toJson());
        }
    }

    /**
     * 用户提现查询
     **/
    public ResultVO<UserBalanceCashQueryVO> userBalanceCashQuery(String batchNo, String orderId, String product){
        LogModel lm = LogModel.newLogModel("userBalanceCashQuery").addStart(String.format("batchNo:%s,orderId:%s,product:%s", batchNo, orderId, product));
        try{
            YopRequest request = new YopRequest(ybConfig.getAppkey(), ybConfig.getShprivatekey());
            request.addParam("batchNo",batchNo);
            request.addParam("orderId",orderId);
            request.addParam("product",product);
            request.addParam("pageNo",1);
            request.addParam("pageSize",100);
            lm.addReq(request.toQueryString());
            YopResponse yopResponse = YopClient3.postRsa(UserBalanceCashQueryUrl, request);
            lm.addRep(yopResponse.toString());
            String state = yopResponse.getState();
            if(Objects.equals(state,"FAILURE")){
                return ResultVO.error(yopResponse.getError().getMessage());
            }
            UserBalanceCashQueryVO userBalanceCashQueryVO = yopResponse.unmarshal(UserBalanceCashQueryVO.class);
            String errorCode = userBalanceCashQueryVO.getErrorCode();
            if(!StringUtil.isEmpty(errorCode)){
                return ResultVO.error(userBalanceCashQueryVO.getErrorMsg());
            }
            return ResultVO.<UserBalanceCashQueryVO>success(userBalanceCashQueryVO);
        }catch(Exception e){
            lm.addException(e.getMessage());
            log.error("请求易宝用户提现异常",e);
            return ResultVO.error("请求易宝用户提现异常");
        }finally {
            log.info(lm.toJson());
        }
    }
}
