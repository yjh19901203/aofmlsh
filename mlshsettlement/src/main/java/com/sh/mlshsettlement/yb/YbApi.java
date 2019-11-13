package com.sh.mlshsettlement.yb;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.sh.mlshcommon.util.JSONUtil;
import com.sh.mlshcommon.util.StringUtil;
import com.sh.mlshcommon.vo.ResultVO;
import com.sh.mlshsettlement.common.LogModel;
import com.sh.mlshsettlement.yb.vo.BalanceCashQueryVO;
import com.sh.mlshsettlement.yb.vo.BalanceCashVO;
import com.yeepay.g3.sdk.yop.client.YopClient3;
import com.yeepay.g3.sdk.yop.client.YopRequest;
import com.yeepay.g3.sdk.yop.client.YopResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@Component
public class YbApi {

    private String BalanceCashUrl = "/rest/v1.0/balance/cash";
    private String BalanceCashQueryUrl = "/rest/v1.0/balance/query-cash-byorder";

    @Resource
    private YbConfig ybConfig;

    /**
     * 提现
     **/
    public ResultVO balanceCash(String merchantId, String orderId, String cashType, BigDecimal amount){
        LogModel lm = LogModel.newLogModel("balanceCash").addStart(String.format("merchantId:%s,orderId:%s,cashType:%s,amount:%s", merchantId, orderId, cashType, amount));
        try{
            if(StringUtil.isEmpty(cashType)){
                cashType = "D1";
            }
            YopRequest request = new YopRequest(ybConfig.getAppkey(), ybConfig.getShprivatekey());
            request.addParam("customerNumber",merchantId);
            request.addParam("amount",amount);
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
    public ResultVO userDeposit(String merchantId, String orderId, String cashType, BigDecimal amount){
        LogModel lm = LogModel.newLogModel("userDeposit").addStart(String.format("merchantId:%s,orderId:%s,cashType:%s,amount:%s", merchantId, orderId, cashType, amount));
        try{
            if(StringUtil.isEmpty(cashType)){
                cashType = "D1";
            }
            YopRequest request = new YopRequest(ybConfig.getAppkey(), ybConfig.getShprivatekey());
            request.addParam("customerNumber",merchantId);
            request.addParam("amount",amount);
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
            log.error("请求易宝用户提现异常",e);
            return ResultVO.error("请求易宝用户提现异常");
        }finally {
            log.info(lm.toJson());
        }
    }

    /**
     * 提现查询
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
}
