package com.sh.mlshsettlement.yb;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.sh.mlshcommon.util.JSONUtil;
import com.sh.mlshcommon.util.ListUtil;
import com.sh.mlshcommon.util.StringUtil;
import com.sh.mlshcommon.vo.ResultVO;
import com.sh.mlshsettlement.common.LogModel;
import com.sh.mlshsettlement.common.logreqaspect.LogRequestAnnotion;
import com.sh.mlshsettlement.yb.vo.*;
import com.yeepay.g3.sdk.yop.client.YopClient;
import com.yeepay.g3.sdk.yop.client.YopClient3;
import com.yeepay.g3.sdk.yop.client.YopRequest;
import com.yeepay.g3.sdk.yop.client.YopResponse;
import com.yeepay.g3.sdk.yop.error.YopError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class YbApi {

    private String BalanceCashUrl = "/rest/v1.0/balance/cash";
    private String BalanceCashQueryUrl = "/rest/v1.0/balance/query-cash-byorder";

    private String UserBalanceCashUrl = "/rest/v1.0/balance/transfer_send";
    private String UserBalanceCashQueryUrl = "/rest/v1.0/balance/transfer_query";
    private String UseBalanceQueryUrl = "/rest/v1.0/balance/query_customer_amount";

//    @Resource
    private YbConfig ybConfig;

    public YbApi(YbConfig ybConfig) {
        this.ybConfig = ybConfig;
    }

    /**
     * 商户钱包提现
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
            if(yopResponse.getResult()==null){
                lm.addEnd("未查询到出款数据");
                return ResultVO.error("未查询到出款数据");
            }
            BalanceCashVO balanceCashVO = JSONUtil.parseObject(yopResponse.getStringResult(), BalanceCashVO.class);
            if(!StringUtil.isEmpty(balanceCashVO.getErrorCode()) && !StringUtil.equals(balanceCashVO.getErrorCode(),"BAC001")){
                lm.addEnd("系统打款状态失败："+balanceCashVO.getErrorCode()+"_"+balanceCashVO.getErrorMsg());
                if(StringUtil.equals(balanceCashVO.getErrorCode(),"BAC409999")){
                    lm.addException("系统异常："+balanceCashVO.getErrorCode()+"____"+balanceCashVO.getErrorMsg());
                    return ResultVO.systemError(balanceCashVO.getErrorMsg());
                }
                return ResultVO.error(balanceCashVO.getErrorMsg());
            }
            return ResultVO.success();
        }catch(Exception e){
            lm.addException(e.getMessage());
            log.error("请求易宝提现异常",e);
            return ResultVO.exceptionError("请求易宝提现异常");
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
            if(Objects.equals(state,"FAILURE")){
                lm.addEnd("调用易宝代付代扣失败："+yopResponse.getError().getMessage());
                return ResultVO.error(yopResponse.getError().getMessage());
            }
            if(yopResponse.getResult()==null){
                lm.addEnd("未查询到出款数据");
                return ResultVO.error("未查询到出款数据");
            }
            UserBalanceCashVO userBalanceCashVO = JSONUtil.parseObject(yopResponse.getStringResult(), UserBalanceCashVO.class);
            String errorCode = userBalanceCashVO.getErrorCode();
            if(!StringUtil.isEmpty(errorCode) && !StringUtil.equals(errorCode,"BAC001")){
                lm.addEnd("系统用户打款状态失败："+userBalanceCashVO.getErrorCode()+"_"+userBalanceCashVO.getErrorMsg());
                if(StringUtil.equals(userBalanceCashVO.getErrorCode(),"BAC409999")){
                    lm.addException("系统异常："+userBalanceCashVO.getErrorCode()+"____"+userBalanceCashVO.getErrorMsg());
                    return ResultVO.systemError(userBalanceCashVO.getErrorMsg());
                }
                return ResultVO.error(userBalanceCashVO.getErrorMsg());
            }
            //判断易宝打款状态
            String transferStatusCode = userBalanceCashVO.getTransferStatusCode();
            if(!YbConstant.TransferStatusEnum.isSuccess(transferStatusCode)){
                lm.addEnd("易宝用户打款状态失败："+transferStatusCode+"_"+userBalanceCashVO.getErrorMsg());
                return ResultVO.error(userBalanceCashVO.getErrorMsg());
            }
            return ResultVO.success();
        }catch(Exception e){
            lm.addException(e.getMessage());
            log.error("请求易宝用户提现异常",e);
            return ResultVO.exceptionError("请求易宝提现异常");
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
                lm.addEnd("连接异常："+yopResponse.getError().getCode()+"___"+yopResponse.getError().getMessage());
                return ResultVO.error(yopResponse.getError().getMessage());
            }
            if(yopResponse.getResult()==null){
                lm.addEnd("未查询到出款数据");
                return ResultVO.error("未查询到出款数据");
            }
            BalanceCashQueryVO balanceCashQueryVO = JSONUtil.parseObject(yopResponse.getStringResult(), BalanceCashQueryVO.class);
            if(!StringUtil.isEmpty(balanceCashQueryVO.getErrorCode()) && !StringUtil.equals(balanceCashQueryVO.getErrorCode(),"BAC001")){
                lm.addEnd("系统打款状态失败："+balanceCashQueryVO.getErrorCode()+"_"+balanceCashQueryVO.getErrorMsg());
                return ResultVO.error(balanceCashQueryVO.getErrorMsg());
            }
            //判断易宝打款状态
            String transferStatusCode = balanceCashQueryVO.getTransferStatusCode();
            if(!YbConstant.TransferStatusEnum.isSuccess(transferStatusCode)){
                lm.addEnd("易宝打款状态失败："+transferStatusCode+"_"+balanceCashQueryVO.getBankMsg());
                return ResultVO.error(balanceCashQueryVO.getBankMsg());
            }
            //判断银行打款状态
            String bankTrxStatusCode = balanceCashQueryVO.getBankTrxStatusCode();
            if(!YbConstant.BankTrxStatusCodeEnum.isSuccess(bankTrxStatusCode)){
                lm.addEnd("银行打款失败："+bankTrxStatusCode+"_"+balanceCashQueryVO.getBankMsg());
                return ResultVO.error(balanceCashQueryVO.getBankMsg());
            }
            if(YbConstant.BankTrxStatusCodeEnum.isProcessing(bankTrxStatusCode)){
                lm.addEnd("银行打款处理中："+bankTrxStatusCode+"_"+balanceCashQueryVO.getBankMsg());
                return ResultVO.processing("银行打款处理中");
            }
            if(!YbConstant.BankTrxStatusCodeEnum.isSuccess(bankTrxStatusCode)){
                lm.addEnd("银行打款失败："+bankTrxStatusCode+"_"+balanceCashQueryVO.getBankMsg());
                return ResultVO.error(balanceCashQueryVO.getBankMsg());
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
                lm.addEnd("查询连接失败");
                return ResultVO.error(yopResponse.getError().getMessage());
            }
            if(yopResponse.getResult()==null){
                lm.addEnd("未查询到出款数据");
                return ResultVO.error("未查询到出款数据");
            }
            UserBalanceCashQueryVO userBalanceCashQueryVO = JSONUtil.parseObject(yopResponse.getStringResult(), UserBalanceCashQueryVO.class);
            lm.addData("userBalanceCashQueryVO",userBalanceCashQueryVO);
            String errorCode = userBalanceCashQueryVO.getErrorCode();
            if(!StringUtil.isEmpty(errorCode) && !StringUtil.equals(errorCode,"BAC001")){
                lm.addEnd("系统打款状态失败："+errorCode+"_"+userBalanceCashQueryVO.getErrorMsg());
                return ResultVO.error(userBalanceCashQueryVO.getErrorMsg());
            }
            List<UserBalanceCashQueryListVO> list = userBalanceCashQueryVO.getList();
            if(ListUtil.isNull(list)){
                lm.addEnd("未查询到打款发起");
                return ResultVO.error("未查询到打款发起");
            }
            //判断易宝打款状态
            UserBalanceCashQueryListVO userBalanceCashQueryListVO = list.get(0);
            String transferStatusCode = userBalanceCashQueryListVO.getTransferStatusCode();
            if(!YbConstant.TransferStatusEnum.isSuccess(transferStatusCode)){
                lm.addEnd("易宝打款状态失败："+transferStatusCode+"_"+userBalanceCashQueryListVO.getBankMsg());
                return ResultVO.error(userBalanceCashQueryListVO.getBankMsg());
            }
            //判断银行打款状态
            String bankTrxStatusCode = userBalanceCashQueryListVO.getBankTrxStatusCode();
            if(!YbConstant.BankTrxStatusCodeEnum.isSuccess(bankTrxStatusCode)){
                lm.addEnd("银行打款失败："+bankTrxStatusCode+"_"+userBalanceCashQueryListVO.getBankMsg());
                return ResultVO.error(userBalanceCashQueryListVO.getBankMsg());
            }
            if(YbConstant.BankTrxStatusCodeEnum.isProcessing(bankTrxStatusCode)){
                lm.addEnd("银行打款处理中："+bankTrxStatusCode+"_"+userBalanceCashQueryListVO.getBankMsg());
                return ResultVO.processing("银行打款处理中");
            }
            if(!YbConstant.BankTrxStatusCodeEnum.isSuccess(bankTrxStatusCode)){
                lm.addEnd("银行打款失败："+bankTrxStatusCode+"_"+userBalanceCashQueryListVO.getBankMsg());
                return ResultVO.error(userBalanceCashQueryListVO.getBankMsg());
            }
            return ResultVO.<UserBalanceCashQueryVO>success(userBalanceCashQueryVO);
        }catch(Exception e){
            lm.addException(e.getMessage());
            log.error("请求易宝用户提现查询异常",e);
            return ResultVO.error("请求易宝用户提现查询异常");
        }finally {
            log.info(lm.toJson());
        }
    }

    /**
     * 查询可用余额
     **/
    public ResultVO queryUseBalance(){
        LogModel lm = LogModel.newLogModel("queryUseBalance");
        try{
            YopRequest request = new YopRequest(ybConfig.getAppkey(), ybConfig.getShprivatekey());
            lm.addReq(request.toQueryString());
            YopResponse yopResponse = YopClient3.postRsa(UseBalanceQueryUrl, request);
//            YopResponse yopResponse = YopClient.post(UseBalanceQueryUrl, request);
            lm.addRep(yopResponse.toString());
            String state = yopResponse.getState();
            if(Objects.equals(state,"FAILURE")){
                return ResultVO.error(yopResponse.getError().getMessage());
            }
            if(yopResponse.getResult()==null){
                lm.addEnd("未查询到出款数据");
                return ResultVO.error("未查询到出款数据");
            }
            UserBalanceCashQueryVO userBalanceCashQueryVO = JSONUtil.parseObject(yopResponse.getStringResult(), UserBalanceCashQueryVO.class);
            String errorCode = userBalanceCashQueryVO.getErrorCode();
            if(!StringUtil.isEmpty(errorCode)){
                return ResultVO.error(userBalanceCashQueryVO.getErrorMsg());
            }
            return ResultVO.success(userBalanceCashQueryVO);
        }catch(Exception e){
            lm.addException(e.getMessage());
            log.error("请求易宝查询可用余额异常",e);
            return ResultVO.error("请求易宝查询可用余额异常");
        }finally {
            log.info(lm.toJson());
        }
    }

    public static void main(String[] args) {
//        YbConfig ybConfig = new YbConfig();
//        ybConfig.setAppkey("OPR:10000466938");
////        ybConfig.setPlateCode("10000466938");
//        ybConfig.setShprivatekey("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDKLgI+64mmJdNg1TwlCPBnNH3b3qfw2TdHVc2uDd4LTyQI8nRr0heFhhdj0OZi6agqekIyzAH/XmO9PdLrTi4YXJXOfiO/dYwKA6gSktRe6FKY4C2WzX1yA4fGfqJMV7RYVoL6In50Hur6rGnavNSQZqbiDJOgy5yokJ14Mey1iMqqqWvADtKN9SqxtbyIxYD/jj/6qLWwmu88wSwSaGdO3wNFgzajsHgRJe9G9IhD0zr5d72HvJGoedq7VaPn3jhIszcPQE6oqbXAddZRGKBehA4WSCjLEl87XH33zZPrxrQlBTHVVGzfxjbB4QvYz0hlEoWh1ntxeDHTfgyhdPQpAgMBAAECggEATmxMSLW6Xe08McpkmwT9ozq0Oy4BvKW1EIGS15nfcEmRc7sAN7Z1k0BxIDGuu91gcqGbvfJuL+0gCQ7LGqTnsmFvZnp9SU3CNTw33ISBxhKdv1jtthodN7Vw3CjQsYYvmThtc7Mfk9FOWk+4e7VVSnHW98XjGbMBIE2AF1heNgeZ40ubdgzuz9+4g4pphjWncPpwcaMfsDZm3JtFyvUp0+LME0CmUqrxvONZAkpFR/PyejGHnIh3ptHzhe/VjNcuIC4PphkCNBakCBCrtohTy0YeeWfDAUTAO4tPXF/JUhlxjPuqR6rpQY/0uQdMAtTpiWHVJar7eGdK81QnuuOFRQKBgQDrklUPM0pkvGG/wREa0bgUI+ki+1/wv7O8X94/8onomJqPpkD8z4hv/Lev/wD5gDcgmgLC36u/XDuhFfVNOmw4eUWenU6pzonroEjhi91AKcRRfzDfOfWg3wPm1J9WQOn5A033tNRydCpVcX/Ot4qDbKcAwLiPNPXXMTn4LUQE/wKBgQDbtmE0KS/kSfjscWJOqwv1XbxckipkxncqIbdiSdU+DzaLd+Vuaco7TLQJRFp7S7WJW4Tz6KBX2UiA7O7ezXY9PwlgXxXiZDDtneXNAqk7DNxmTTZHrF2C7qdU98klppCFiFx9bysGY6lFWofWmg3Pu5IiPqO3iLRPTvZgQOE+1wKBgQC9SCgmfYzyIlfcjtIinY5uSGiEnjz5od9WpiVbdpOPHEdc0zZ2rH6xlPs3ZAuxbm9dN8KuOLC0ovSau50Nv7rDKdZh234gfP9fH7xP1mUhsC25Why30MdnyqpE6GVbFe+qERitx1PI30RAwWDzhZC7hystNK1XDDPZBAnTOvPjmwKBgDFuujX7IkxRnFDOPdkHQNyGp2+Ib0NXJ85x4YmapQCeeZ4tbpBF+vsWidcf6t+crA5oaeRarWC2gUqIhEHapkSnXxuwqQLTmfKMOPzEIYEoppnZu2Gq1Ss1OK60RSxUamWwxWZvUZXRbG8vLCrLZFodkIZl433SowbI9EO5tTPnAoGAJRsy1z95Q1GPkKrFtKivkxZy1k7zJXjM0VWDc7lT9fBnoeGUyt+vuq+lC5i2aiWKJK7pe8MM9QFDGlWPnly+J8jbyMfm99k5oJtCWDfF0or1pAQ4mw0kjL9TvDVXdojgYA+rxSMQ09hwsYukQ4bblrwfBUmRjLN5WibcRzIW5ZA=");
//        YbApi ybApi = new YbApi(ybConfig);
////        ResultVO resultVO = ybApi.userDeposit("1111111112111112", "张三", BigDecimal.valueOf(0.01), "4567898", "CMBCHINA", "", "", "");
////        System.out.println("---------------"+resultVO);
//        ResultVO<UserBalanceCashQueryVO> userBalanceCashQueryVOResultVO = ybApi.userBalanceCashQuery("1111111112111112", "1111111112111112", "WTJS");
////        ResultVO userBalanceCashQueryVOResultVO = ybApi.queryUseBalance();
//        System.out.println("======="+userBalanceCashQueryVOResultVO);

        mchTixian();
    }

    /**
     *
     **/
    public static void mchTixian(){
        YbConfig ybConfig = new YbConfig();
        ybConfig.setAppkey("OPR:10025454402");
//        ybConfig.setPlateCode("10000466938");
        ybConfig.setShprivatekey("MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCQp9ZAjAdKherqhniyD01BaDWS3vv7yoUfoZNZEDFZjT3t0KFl68wpnlUqIWUgX5XDY8i1qxBud918bH/SfqEWvXwfv6Bvsuy6f2sgpmsentaMSgd0TCsKmdnQEZUs54bK5r7QG6/7nRhJKiiNxMykSB1naTYmCzwm2EE2zfje2GEXUnEkVUwX8s/hVc76faeU4aDruagHKKwTeSXK8PqQcWqTvCXtMPHr8eNLwnAzOS9GVxiXns8BVsUGL3AXiOCx91Rj2pIcuKNb2NKczg4BBLdFrn8FH0fOX6u/Yiqpwkfs2LVRQ76A5ZN1cE1YSSz64Unz88zWqT6KdyOC58GDAgMBAAECggEAW+p5xnvzqhPcCHe3u5TjhxsbZ57OyNiPrGCzyxTx9rA72KAH3kZtbUiRXvrnUiHKfOQrxdvTzg3WK9iZg/w4icWPWHEgFbnAmGScgpucV1GgjHYBtJEtjEfkUkTJ4EWhrc3fE0d0lFHm+56RNDwHuJ+mJ26AHL5o01n4XkWUzKwE7xDs1+3rQqpEecGd1UAEeh0hdhMPOvbk76Ed5Nwc0KCi279EX7+46rl43ucVT1O82Kn/ea5SW91h2pHP7Z2Vqu/ClwiMHtOB1GaIHkIQqZNbSD/JKUvVEQbWX+EciuBYCFCSlUa4iXqNJBKD0018O6zI95GjmTo5VTAVN9ttyQKBgQDZVbfGwx+nJrGuu0k6EbrgDAwHctHyBzQFSAcZu3QHw1jlz6qwRK0H9iAUQ+Ai1EhqZc2Hc65UUsVNvqrg7B14E7Qa/E7gP4w0t+RZjmM3HJ1SGh0A9vCIBlHJU8dD3B83SFRNuB6A97VaMf7y6LIIiE2WLlVmxwFvdhczFmRXxwKBgQCqZAWSK/rZOSVltI9Ky77goLUeZ4XgvK7Soqmnc2c5pxCK1lZ4eUVuz5kkxH7XNRgfzVuv90RMzuSxQbRPVmsJvzXkqzEcTKE/usD43r+h3Cj0vCgrygaBZDUPWRLUlQmnqGrJ6OtrECASBr0JIVsCraw1VZVH6igqQ0cOyWXgZQKBgQCkQI4l3636a9n5jn3nLyMm1LP6pSJaYmtEWo1TO0KT42U6OdpsYVrMG41fA3VPnVQlZU8RwbHM9Mk82o7WclxLNcj2ZB1QiXZzGU+xB1WgfzW+qRGmevqKMwx4taJxfNjytO1R14bmPU+IHSTrfEWhATJyAA53IIvLK1qZN6EaXQKBgE768gIhVLNf48/S9CCtdJrWo12mis3zZ+3G7HDr80sr/2T7mTVasS5+F/SNCGZk+/uxyvAz43re4+6uEBA+dHseILMQD0GY252YyV4Ski1Kyck3dj9l88ICWv30QeA7/S+zx1w0FPuZi+QPL4yN44vISOyn8PxWs6tzZ/qYrBppAoGAIrE4q6KxzopHkX2d1BaxV0PMUYZe7NasvkQDl7t7kkgeQRiEDHzHXVDElWdXi4DIWA9wK2SIBemKdR3gasy9PNi78Ac6D0bxoelxHo6js+yEeoG80g2xEDwYO8Ceor06TUFbHrjlMejM0XiiitJ7jJTzW15g8SRWqgaDIEg6KxU=");
        YbApi ybApi = new YbApi(ybConfig);
        ResultVO<BalanceCashQueryVO> balanceCashQueryVOResultVO = ybApi.balanceCashQuery("10029573096", "1", "D1");
        System.out.println("========="+JSONObject.toJSONString(balanceCashQueryVOResultVO));
    }
}
