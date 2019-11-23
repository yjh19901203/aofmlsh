package com.sh.mlshsettlement.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sh.mlshcommon.util.ListUtil;
import com.sh.mlshcommon.util.ThreadPoolUtil;
import com.sh.mlshcommon.vo.ResultVO;
import com.sh.mlshsettlement.common.LogModel;
import com.sh.mlshsettlement.exception.BusinessException;
import com.sh.mlshsettlement.model.SettleFlowing;
import com.sh.mlshsettlement.model.SettleMch;
import com.sh.mlshsettlement.mapper.SettleMchMapper;
import com.sh.mlshsettlement.model.TradeInfo;
import com.sh.mlshsettlement.service.ISettleFlowingService;
import com.sh.mlshsettlement.service.ISettleMchService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sh.mlshsettlement.service.ITradeInfoService;
import com.sh.mlshsettlement.vo.SummaryTradeInfoVO;
import com.sh.mlshsettlement.yb.YbApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class SettleMchServiceImpl extends ServiceImpl<SettleMchMapper, SettleMch> implements ISettleMchService {

    @Resource
    private ITradeInfoService tradeInfoService;
    @Resource(name = "dataSourceTransactionManager")
    private PlatformTransactionManager dataSourceTransactionManager;
    @Resource
    private ISettleFlowingService settleFlowingService;
    @Resource
    private YbApi ybApi;


    @Override
    public void summaryMchSettle(LocalDate day) {
        Long id = 0L;
        //汇总订单数据
        while (true){
            List<SummaryTradeInfoVO> summaryTradeInfoVOS = tradeInfoService.summaryTradeInfo(day, id);
            if(ListUtil.isNull(summaryTradeInfoVOS)){
                return;
            }
            //添加汇总数据
            ThreadPoolUtil.execute(new Runnable() {
                @Override
                public void run() {
                    TransactionStatus transaction = dataSourceTransactionManager.getTransaction(TransactionDefinition.withDefaults());
                    try{
                        //批量添加汇总数据
                        batchInsertSummaryTrade(summaryTradeInfoVOS);
                        //更新订单ID
                        updateTradeInfoId(summaryTradeInfoVOS);

                        dataSourceTransactionManager.commit(transaction);
                    }catch(Exception e){
                        dataSourceTransactionManager.rollback(transaction);
                        log.error("批量添加结算数据出现异常",e);
                    }
                }
            });
            if(summaryTradeInfoVOS.size()<2000){
                return;
            }
            id = summaryTradeInfoVOS.get(summaryTradeInfoVOS.size()-1).getMaxId();
        }
    }

    private void updateTradeInfoId(List<SummaryTradeInfoVO> summaryTradeInfoVOS) {
        summaryTradeInfoVOS.stream().forEach(summaryTradeInfoVO -> {
            try{
                String tradeInfoId = summaryTradeInfoVO.getTradeInfoId();
                LambdaQueryWrapper<SettleMch> query = new QueryWrapper<SettleMch>().lambda().eq(SettleMch::getMchId, summaryTradeInfoVO.getMchId())
                        .eq(SettleMch::getYbMchId, summaryTradeInfoVO.getYbMchId())
                        .eq(SettleMch::getSummaryDay, summaryTradeInfoVO.getSummaryDay());
                SettleMch settleMch = baseMapper.selectOne(query);
                if(settleMch==null){
                    throw new BusinessException("查询商户结算信息为空："+summaryTradeInfoVO.getYbMchId() + "-" +summaryTradeInfoVO.getSummaryDay());
                }
                String[] idList = tradeInfoId.split(",");
                List<Long> ids = Arrays.stream(idList).map(id -> Long.valueOf(id)).collect(Collectors.toList());
                int skip = 0;
                int limit = 100;
                List<List> listid = new ArrayList<List>();
                while (true){
                    List<Long> list = ids.stream().skip(skip).limit(limit).collect(Collectors.toList());
                    if(!ListUtil.isNull(list)){
                        listid.add(list);
                    }
                    if(ListUtil.isNull(list) || list.size()<limit){
                        break;
                    }
                    skip = skip + limit;
                }
                listid.stream().forEach(list -> {
                    tradeInfoService.updateSummaryTradeInfo(list,settleMch.getBatchNo(), TradeInfo.StatusEnum.s_1.getCode());
                });
            }catch(Exception e){
                log.error("更新交易订单异常",e);
                if(e instanceof BusinessException){
                    throw e;
                }
            }
        });

    }

    private void batchInsertSummaryTrade(List<SummaryTradeInfoVO> summaryTradeInfoVOS) {
        int limit = 300;
        for (int skip = 0; ; skip = skip+limit) {
            List<SummaryTradeInfoVO> summaryTradeInfos = summaryTradeInfoVOS.stream().skip(skip).limit(limit).collect(Collectors.toList());
            if(ListUtil.isNull(summaryTradeInfos)){
                return;
            }
            baseMapper.batchInsertOrUpdate(summaryTradeInfos);
            if(summaryTradeInfos.size()<limit){
                return;
            }
        }

    }

    @Override
    public void settleMch(LocalDate day) {
        Long id = 0L;
        while (true){
            List<SettleMch> settleMches = baseMapper.pageSelect(day, id);
            if(ListUtil.isNull(settleMches)){
                return;
            }
            settleMches.stream().forEach(settleMch -> {
                try{
                    settleMchOne(settleMch);
                }catch(Exception e){
                    log.error("商户请求提现异常:"+settleMch,e);
                }
            });
            if(settleMches.size()<2000){
                return;
            }
            id = settleMches.get(settleMches.size()-1).getId();
        }
    }

    private void settleMchOne(SettleMch settleMch) {
        LogModel lm = LogModel.newLogModel("settleMchOne").addStart(settleMch);
        TransactionStatus transaction = dataSourceTransactionManager.getTransaction(TransactionDefinition.withDefaults());
        try{
            String flowing = settleFlowingService.insertFlowing(SettleFlowing.SettleSourceEnum.s_1.getCode(), settleMch.getId(), settleMch.getYbMchId(), settleMch.getSettleAmount(),"");
            //更新商户打款中
            if(!updateById(new SettleMch(settleMch.getId(), SettleMch.PayStatusEnum.p_1.getCode(), new Date()))){
                lm.addEnd("更新商户结算单失败:"+settleMch);
                dataSourceTransactionManager.rollback(transaction);
                return;
            };
            //更新tradeinfo数据
            updateTradeInfoStatus(settleMch.getBatchNo(), TradeInfo.StatusEnum.s_2.getCode());
            //调用易宝打款
            ResultVO resultVO = ybApi.balanceCash(settleMch.getYbMchId(), flowing, "D1", settleMch.getSettleAmount());
            if(!resultVO.isSuccess()){
                lm.addEnd("调用易宝接口失败："+resultVO.getMsg());
                settleFlowingService.updateFlowingFail(flowing,resultVO.getMsg(),null);
                dataSourceTransactionManager.commit(transaction);
                return;
            }
            dataSourceTransactionManager.commit(transaction);
        }catch(Exception e){
            log.error("发起单个打款异常:"+settleMch,e);
            dataSourceTransactionManager.rollback(transaction);
        }finally {
            log.info(lm.toJson());
        }
    }

    private void updateTradeInfoStatus(Long batchNo, Integer code) {
        tradeInfoService.updateTradeInfoStatus(batchNo,code);
    }

}
