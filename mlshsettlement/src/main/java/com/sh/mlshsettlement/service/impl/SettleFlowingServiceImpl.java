package com.sh.mlshsettlement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sh.mlshcommon.util.DateUtil;
import com.sh.mlshcommon.util.IdGenerator;
import com.sh.mlshcommon.util.ListUtil;
import com.sh.mlshcommon.util.StringUtil;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

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
    public String insertFlowing(Integer settleSource, Long settleSign, String settleMch, BigDecimal settleAmount) {
        String flowingid = IdGenerator.generatorId(settleSource + "", 15);
        boolean save = save(new SettleFlowing(flowingid, settleSource, settleSign, settleMch, settleAmount));
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
            String flowing = settleFlowing.getSettleFlowing();
            ResultVO<BalanceCashQueryVO> resultVO = ybApi.balanceCashQuery(settleFlowing.getSettleMch(), flowing, null);
            if(!resultVO.isSuccess()){
                updateFlowingFail(flowing,resultVO.getMsg());
            }else{
                updateFlowingSuccess(flowing,resultVO.getData());
            }
            //更新对应数据
            settleMap.get(settleFlowing.getSettleSource()).invoke(this,settleFlowing.getSettleSign(),resultVO);
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
    public void updateFlowingFail(String flowing, String msg) {
        UpdateWrapper<SettleFlowing> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(SettleFlowing::getSettleFlowing,flowing)
        .set(SettleFlowing::getSettleStatus, SettleFlowing.FlowingSettleStatusEnum.s_3.getCode())
        .set(SettleFlowing::getSettleDesc,msg);
        update(updateWrapper);
    }

    /**
     * 商户结算结果处理
     **/
    @SettleResultAnnotion(source = 1)
    public void mchSettleResult(Long sign,ResultVO<BalanceCashQueryVO> resultVO){
        String payAccount = "";
        String payAccountName = "";
        BigDecimal realPayAmount = BigDecimal.ZERO;
        String payDesc = "";
        Date payTime = null;
        Integer settleStatus = SettleMch.SettleStatusEnum.s_1.getCode();
        Integer payStatus = SettleMch.PayStatusEnum.p_1.getCode();
        Integer tradeInfoStatus = SettleMch.PayStatusEnum.p_1.getCode();
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
    public void userSettleResult(Long sign,ResultVO<BalanceCashQueryVO> resultVO){
        // TODO: 2019/11/7 通知调用方结果
        String payAccount = "";
        String payAccountName = "";
        BigDecimal realPayAmount = BigDecimal.ZERO;
        String payDesc = "";
        Date payTime = null;
        Integer settleStatus = SettleMch.SettleStatusEnum.s_1.getCode();
        Integer payStatus = SettleMch.PayStatusEnum.p_1.getCode();
        Integer tradeInfoStatus = SettleMch.PayStatusEnum.p_1.getCode();
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

        // TODO: 2019/11/13 通知打款调用方结果

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

    @Override
    public ResultVO userDeposit(Long userId, String ybMerchantNo, Long transactionId, BigDecimal amount) {
        LogModel lm = LogModel.newLogModel("userDeposit").addStart(String.format("userId:%s,ybMerchantNo:%s,transactionId:%s,amount:%s", userId, ybMerchantNo, transactionId, amount));
        String flowing = insertFlowing(SettleFlowing.SettleSourceEnum.s_2.getCode(), transactionId, ybMerchantNo, amount);
        //调用易宝打款
        ResultVO resultVO = ybApi.userDeposit(ybMerchantNo, flowing, null, amount);
        if(!resultVO.isSuccess()){
            lm.addEnd("调用易宝用户提现接口失败："+resultVO.getMsg());
            updateFlowingFail(flowing,resultVO.getMsg());
            return resultVO;
        }
        return ResultVO.success();
    }
}
