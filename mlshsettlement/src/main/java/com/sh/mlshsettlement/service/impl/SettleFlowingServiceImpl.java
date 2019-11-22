package com.sh.mlshsettlement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sh.mlshcommon.util.*;
import com.sh.mlshcommon.util.okhttp.OkHttpUtil;
import com.sh.mlshcommon.vo.ResultVO;
import com.sh.mlshsettlement.annotion.SettleResultAnnotion;
import com.sh.mlshsettlement.common.LogModel;
import com.sh.mlshsettlement.exception.BusinessException;
import com.sh.mlshsettlement.mapper.SettleMchMapper;
import com.sh.mlshsettlement.mapper.TradeInfoMapper;
import com.sh.mlshsettlement.model.SettleFlowing;
import com.sh.mlshsettlement.mapper.SettleFlowingMapper;
import com.sh.mlshsettlement.model.SettleMch;
import com.sh.mlshsettlement.model.TradeInfo;
import com.sh.mlshsettlement.service.ISettleFlowingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sh.mlshsettlement.yb.YbApi;
import com.sh.mlshsettlement.yb.vo.BalanceCashQueryVO;
import com.sh.mlshsettlement.yb.vo.UserBalanceCashQueryListVO;
import com.sh.mlshsettlement.yb.vo.UserBalanceCashQueryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jinghui.yu
 * @since 2019-11-05
 */
@Slf4j
@Service
public class SettleFlowingServiceImpl extends ServiceImpl<SettleFlowingMapper, SettleFlowing> implements ISettleFlowingService {

    @Resource
    private YbApi ybApi;
    @Resource
    private SettleMchMapper settleMchMapper;
    @Resource(name = "dataSourceTransactionManager")
    private PlatformTransactionManager dataSourceTransactionManager;
    @Resource
    private TradeInfoMapper tradeInfoMapper;

    private Map<Integer, Method> settleMap = new HashMap<>();

    @PostConstruct
    public void init(){
        Method[] methods = this.getClass().getDeclaredMethods();
        Arrays.stream(methods).filter(method -> method.isAnnotationPresent(SettleResultAnnotion.class)).collect(Collectors.toList()).forEach(method -> {
            int source = method.getAnnotation(SettleResultAnnotion.class).source();
            settleMap.put(source,method);
        });
    }

    @Override
    public String insertFlowing(Integer settleSource, Long settleSign, String settleMch, BigDecimal settleAmount,String notifyUrl) {
        String flowingid = IdGenerator.generatorId(settleSource + "", 15);
        boolean save = save(new SettleFlowing(flowingid, settleSource, settleSign, settleMch, settleAmount,notifyUrl, SettleFlowing.NotifyStatusEnum.s_1.getCode()));
        if(!save){
            throw new BusinessException("添加流水失败");
        }
        return flowingid;
    }

    @Override
    public void queryFlowingResultAll(Integer source,Long sign) {
        Long id = 0L;
        while (true){
            List<SettleFlowing> settleFlowings = baseMapper.pageList(id,source,sign);
            if(ListUtil.isNull(settleFlowings)){
                return;
            }
            settleFlowings.stream().forEach(settleFlowing -> updateSettleFlowing(settleFlowing));
            if(settleFlowings.size()<2000){
                return;
            }
            id = settleFlowings.get(settleFlowings.size()-1).getId();
        }
    }

    private void updateSettleFlowing(SettleFlowing settleFlowing) {
        TransactionStatus transaction = dataSourceTransactionManager.getTransaction(TransactionDefinition.withDefaults());
        try{
//            String flowing = settleFlowing.getSettleFlowing();
//            ResultVO<BalanceCashQueryVO> resultVO = ybApi.balanceCashQuery(settleFlowing.getSettleMch(), flowing, null);
//            if(!resultVO.isSuccess()){
//                updateFlowingFail(flowing,resultVO.getMsg());
//            }else{
//                updateFlowingSuccess(flowing,resultVO.getData());
//            }
            //更新对应数据
            settleMap.get(settleFlowing.getSettleSource()).invoke(this,settleFlowing.getSettleSign(),settleFlowing);
            dataSourceTransactionManager.commit(transaction);
        }catch(Exception e){
            log.error("查询打款结果异常");
            dataSourceTransactionManager.rollback(transaction);
        }

    }

    private void updateFlowingSuccess(String flowing, BalanceCashQueryVO balanceCashQueryVO) {

        Integer settleStatus = BalanceCashQueryVO.getSettleStatus(balanceCashQueryVO.getTransferStatusCode(), balanceCashQueryVO.getBankTrxStatusCode());
        String bankMsg = balanceCashQueryVO.getBankMsg();
        if(StringUtil.isEmpty(bankMsg)){
            bankMsg = SettleFlowing.FlowingSettleStatusEnum.getNameByCode(settleStatus);
        }

        UpdateWrapper<SettleFlowing> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(SettleFlowing::getSettleFlowing,flowing)
                .set(SettleFlowing::getSettleStatus, settleStatus)
                .set(SettleFlowing::getSettleDesc,bankMsg)
                .set(SettleFlowing::getSettleAccountName,balanceCashQueryVO.getBankAccountName())
                .set(SettleFlowing::getSettleAccountNo,balanceCashQueryVO.getBankAccountNo())
                .set(SettleFlowing::getPayTime,DateUtil.parseDate(balanceCashQueryVO.getFinishTime(),DateUtil.YYYYMMDDHHSSMM))
                .set(SettleFlowing::getRealPayAmount,balanceCashQueryVO.getRealAmount());
        update(updateWrapper);
    }

    @Override
    public void updateFlowingFail(String flowing, String msg,Integer notifyStatus) {
        LambdaUpdateWrapper<SettleFlowing> updateWrapper = new LambdaUpdateWrapper<SettleFlowing>();
        updateWrapper.eq(SettleFlowing::getSettleFlowing,flowing)
        .set(SettleFlowing::getSettleStatus, SettleFlowing.FlowingSettleStatusEnum.s_3.getCode())
        .set(SettleFlowing::getSettleDesc,msg);
        if(notifyStatus!=null){
            updateWrapper.set(SettleFlowing::getNotifyStatus,notifyStatus);
        }
        update(updateWrapper);
    }

    /**
     * 商户结算结果处理
     **/
    @SettleResultAnnotion(source = 1)
    public void mchSettleResult(Long sign,SettleFlowing settleFlowing){
        String payAccount = "";
        String payAccountName = "";
        BigDecimal realPayAmount = BigDecimal.ZERO;
        String payDesc = "";
        Date payTime = null;
        Integer settleStatus = SettleMch.SettleStatusEnum.s_1.getCode();
        Integer payStatus = SettleMch.PayStatusEnum.p_1.getCode();
        Integer tradeInfoStatus = SettleMch.PayStatusEnum.p_1.getCode();
        String flowing = settleFlowing.getSettleFlowing();
        ResultVO<BalanceCashQueryVO> resultVO = ybApi.balanceCashQuery(settleFlowing.getSettleMch(), flowing, "D1");
        if(!resultVO.isSuccess()){
            updateFlowingFail(flowing,resultVO.getMsg(),null);
        }else{
            updateFlowingSuccess(flowing,resultVO.getData());
        }
        if(!resultVO.isSuccess()){
            payTime = new Date();
            payStatus = SettleMch.PayStatusEnum.p_2.getCode();
            tradeInfoStatus = TradeInfo.SettleStatusEnum.getTradeCodeByCode(payStatus);
            payDesc = resultVO.getMsg();
        }else{
            BalanceCashQueryVO balanceCashQueryVO = resultVO.getData();
            payStatus = BalanceCashQueryVO.getSettleStatus(balanceCashQueryVO.getTransferStatusCode(), balanceCashQueryVO.getBankTrxStatusCode());
            tradeInfoStatus = TradeInfo.SettleStatusEnum.getTradeCodeByCode(payStatus);
            payDesc = balanceCashQueryVO.getBankMsg();
            if(StringUtil.isEmpty(payDesc)){
                payDesc = SettleFlowing.FlowingSettleStatusEnum.getNameByCode(settleStatus);
            }
            payAccountName = balanceCashQueryVO.getBankAccountName();
            payAccount = balanceCashQueryVO.getBankAccountNo();
            payTime = DateUtil.parseDate(balanceCashQueryVO.getFinishTime(),DateUtil.YYYYMMDDHHSSMM);
            realPayAmount = balanceCashQueryVO.getRealAmount();
            if(payStatus.intValue()== SettleMch.PayStatusEnum.p_3.getCode().intValue()){
                settleStatus = SettleMch.SettleStatusEnum.s_2.getCode();
            }
        }
        SettleMch settleMch = new SettleMch(sign,realPayAmount,settleStatus,payStatus,payTime,payDesc,payAccount,payAccountName);
        settleMchMapper.updateById(settleMch);

        //更新相应订单数据
        TradeInfo tradeInfo = new TradeInfo(sign,tradeInfoStatus);
        LambdaUpdateWrapper<TradeInfo> updateWapper = new UpdateWrapper<TradeInfo>().lambda().eq(TradeInfo::getBatchNo, sign);
        tradeInfoMapper.update(tradeInfo,updateWapper);
    }

    /**
     * 用户结算结果处理
     **/
    @SettleResultAnnotion(source = 2)
    public void userSettleResult(Long sign,SettleFlowing settleFlowing){

        String flowing = settleFlowing.getSettleFlowing();
        ResultVO<UserBalanceCashQueryVO> resultVO = ybApi.userBalanceCashQuery(settleFlowing.getSettleSign() + "", settleFlowing.getSettleSign() + "", "WTJS");
        if(resultVO.isFail()){
            updateFlowingFail(flowing,resultVO.getMsg(),null);
        }else if(resultVO.isSuccess()){
            updateUserFlowingSuccess(flowing,resultVO.getData());
        }
        //通知出款发起方
        notifyUserSettle(settleFlowing.getNotifyUrl(),sign);

//        UserBalanceCashQueryVO userBalanceCashQueryVO = resultVO.getData();
//        if (userBalanceCashQueryVO.getTotalCount()==0) {
//            log.info("未查询到打款数据结果");
//            return;
//        }
//        UserBalanceCashQueryListVO userBalanceCashQueryListVO = userBalanceCashQueryVO.getList().get(0);
//        //通知调用方结果
//        String payAccount = "";
//        String payAccountName = "";
//        BigDecimal realPayAmount = BigDecimal.ZERO;
//        String payDesc = "";
//        Date payTime = null;
//        Integer settleStatus = SettleMch.SettleStatusEnum.s_1.getCode();
//        Integer payStatus = SettleMch.PayStatusEnum.p_1.getCode();
//        Integer tradeInfoStatus = SettleMch.PayStatusEnum.p_1.getCode();
//        if(!resultVO.isSuccess()){
//            payTime = new Date();
//            payStatus = SettleMch.PayStatusEnum.p_2.getCode();
//            tradeInfoStatus = TradeInfo.SettleStatusEnum.getTradeCodeByCode(payStatus);
//            payDesc = resultVO.getMsg();
//        }else{
//            payStatus = BalanceCashQueryVO.getSettleStatus(userBalanceCashQueryListVO.getTransferStatusCode(), userBalanceCashQueryListVO.getBankTrxStatusCode());
//            tradeInfoStatus = TradeInfo.SettleStatusEnum.getTradeCodeByCode(payStatus);
//            payDesc = userBalanceCashQueryListVO.getBankMsg();
//            if(StringUtil.isEmpty(payDesc)){
//                payDesc = SettleFlowing.FlowingSettleStatusEnum.getNameByCode(settleStatus);
//            }
//            payAccountName = userBalanceCashQueryListVO.getAccountName();
//            payAccount = userBalanceCashQueryListVO.getAccountNumber();
//            payTime = DateUtil.parseDate(userBalanceCashQueryListVO.getFinishDate(),DateUtil.YYYYMMDDHHSSMM2);
//            realPayAmount = StringUtil.isEmpty(userBalanceCashQueryListVO.getSuccessAmount())?BigDecimal.ZERO:new BigDecimal(userBalanceCashQueryListVO.getSuccessAmount());
//            if(payStatus.intValue()== SettleMch.PayStatusEnum.p_3.getCode().intValue()){
//                settleStatus = SettleMch.SettleStatusEnum.s_2.getCode();
//            }
//        }
//        // TODO: 2019/11/13 通知打款调用方结果

    }

    private void updateUserFlowingSuccess(String flowing, UserBalanceCashQueryVO userBalanceCashQueryVO) {
        if (userBalanceCashQueryVO.getTotalCount()==0) {
            log.info("未查询到打款数据结果");
            return;
        }
        UserBalanceCashQueryListVO userBalanceCashQueryListVO = userBalanceCashQueryVO.getList().get(0);
        Integer settleStatus = BalanceCashQueryVO.getSettleStatus(userBalanceCashQueryListVO.getTransferStatusCode(), userBalanceCashQueryListVO.getBankTrxStatusCode());
        String bankMsg = userBalanceCashQueryListVO.getBankMsg();
        if(StringUtil.isEmpty(bankMsg)){
            bankMsg = SettleFlowing.FlowingSettleStatusEnum.getNameByCode(settleStatus);
        }

        UpdateWrapper<SettleFlowing> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(SettleFlowing::getSettleFlowing,flowing)
                .set(SettleFlowing::getSettleStatus, settleStatus)
                .set(SettleFlowing::getSettleDesc,bankMsg)
                .set(SettleFlowing::getSettleAccountName,userBalanceCashQueryListVO.getAccountName())
                .set(SettleFlowing::getSettleAccountNo,userBalanceCashQueryListVO.getAccountNumber())
                .set(SettleFlowing::getPayTime,DateUtil.parseDate(userBalanceCashQueryListVO.getFinishDate(),DateUtil.YYYYMMDDHHSSMM2))
                .set(SettleFlowing::getRealPayAmount,userBalanceCashQueryListVO.getSuccessAmount());
        update(updateWrapper);
    }

    /**
     * 平台结算结果处理
     **/
    @SettleResultAnnotion(source = 3)
    public void plateSettleResult(Long sign,ResultVO<BalanceCashQueryVO> resultVO){
        // TODO: 2019/11/7 出款结果待确认
    }

    @Override
    public SettleFlowing queryFlowingResult(Integer settleSource, Long settleSign) {
        LambdaQueryWrapper<SettleFlowing> settleFlowingLambdaQueryWrapper = new QueryWrapper<SettleFlowing>().lambda().eq(SettleFlowing::getSettleSource, settleSource)
                .eq(SettleFlowing::getSettleSign, settleSign)
                .orderByDesc(SettleFlowing::getId);
        List<SettleFlowing> settleFlowings = baseMapper.selectList(settleFlowingLambdaQueryWrapper);
        if(ListUtil.isNull(settleFlowings)){
            return null;
        }
        return settleFlowings.get(0);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVO userDeposit(Long requestNo, String accountName, BigDecimal amount,String accountNumber,String bankCode,Long userId,String bankBranchName,String provinceCode,String cityCode,String notifyUrl) {
        LogModel lm = LogModel.newLogModel("userDeposit").addStart(String.format("requestNo:%s,accountName:%s,amount:%s,accountNumber:%s,bankCode:%s,userId:%s", requestNo, accountName, amount,accountNumber,bankCode,userId));
        String flowing = insertFlowing(SettleFlowing.SettleSourceEnum.s_2.getCode(), requestNo, userId+"", amount,notifyUrl);
        //调用易宝用户打款
        ResultVO resultVO = ybApi.userDeposit(flowing, accountName, amount,accountNumber,bankCode,bankBranchName,provinceCode,cityCode);
        resultVO = ResultVO.success();
        if(!resultVO.isSuccess()){
            lm.addEnd("调用易宝用户提现接口失败："+resultVO.getMsg());
            updateFlowingFail(flowing,resultVO.getMsg(), SettleFlowing.NotifyStatusEnum.s_2.getCode());
        }
        return resultVO;
    }

    /**
     * 通知用户提现结果
     */
    public void notifyUserSettle(String url,Long merchOrderNo){
        LogModel lm = LogModel.newLogModel("notifyUserSettle").addStart(String.format("url:%s,merchOrderNo:%s", url, merchOrderNo));
        try{
            LambdaQueryWrapper<SettleFlowing> eq = new QueryWrapper<SettleFlowing>().lambda()
                    .eq(SettleFlowing::getSettleSign, merchOrderNo)
                    .eq(SettleFlowing::getSettleSource, SettleFlowing.SettleSourceEnum.s_2.getCode());
            SettleFlowing settleFlowing = getOne(eq);
            String serviceStatus = "REMITTANCE_SUCCESS";
            String serviceMsg = "";
            if(settleFlowing==null){
                serviceStatus = "REMITTANCE_FAIL";
                serviceMsg = "未查询到发起数据";
            }
            if(Objects.equals(settleFlowing.getSettleStatus(), SettleFlowing.FlowingSettleStatusEnum.s_1.getCode())){
                return;
            }
            serviceStatus = SettleFlowing.MlSettleStatusEnum.getMlCodeByCode(settleFlowing.getSettleStatus());
            serviceMsg = settleFlowing.getSettleDesc();
            Map<String,String> map = new HashMap<String,String>();
            map.put("merchOrderNo",merchOrderNo+"");
            map.put("serviceStatus",serviceStatus);
            map.put("serviceMsg",serviceMsg);
            lm.addReq(url+"_____"+ JSONUtil.toString(map));
            String response = OkHttpUtil.postFormParams(url, map);
            lm.addRep(response);
            if(!StringUtil.isEmpty(response) && Objects.equals(response,"SUCCESS")){
                settleFlowing.setNotifyStatus(SettleFlowing.NotifyStatusEnum.s_2.getCode());
            }else{
                settleFlowing.setNotifyStatus(SettleFlowing.NotifyStatusEnum.s_3.getCode());
            }
            updateById(settleFlowing);
        }catch(Exception e){
            lm.addException(e.getMessage());
            log.error("调用三方接口异常：",e);
        }finally {
            log.info(lm.toJson());
        }
    }

    @Override
    public void userNotify() {
        LambdaQueryWrapper<SettleFlowing> wrapper = new LambdaQueryWrapper<SettleFlowing>()
                .eq(SettleFlowing::getSettleSource, SettleFlowing.SettleSourceEnum.s_2.getCode())
                .eq(SettleFlowing::getNotifyStatus, SettleFlowing.NotifyStatusEnum.s_3.getCode())
                .ge(SettleFlowing::getCreateTime,DateUtil.addDate(new Date(),-2));
        List<SettleFlowing> list = list(wrapper);
        if(ListUtil.isNull(list)){
            return;
        }
        list.stream().forEach(l -> {
            LogModel lm = LogModel.newLogModel("通知打款方").addStart(l);
            try{
                String notifyUrl = l.getNotifyUrl();
                if(StringUtil.isEmpty(notifyUrl)){
                    lm.addEnd("通知调用方打款回调地址为空");
                    return;
                }
                notifyUserSettle(notifyUrl,l.getSettleSign());
            }catch(Exception e){
                log.error("通知调用方打款情况异常",e);
                lm.addException("通知调用方打款情况异常:"+e.getMessage());
            }finally {
                log.info(lm.toJson());
            }
        });
    }
}
