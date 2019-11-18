package com.sh.mlshjob.controller;


import com.sh.mlshcommon.vo.ResultVO;
import com.sh.mlshjob.controller.po.DepositPO;
import com.sh.mlshsettlement.common.logaspect.LogAnnotion;
import com.sh.mlshsettlement.model.SettleFlowing;
import com.sh.mlshsettlement.service.ISettleFlowingService;
import com.sh.mlshsettlement.vo.DepositVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jinghui.yu
 * @since 2019-11-05
 */
@Controller
@RequestMapping("/api/v1/settleflowing")
public class SettleFlowingController {

    @Resource
    private ISettleFlowingService settleFlowingService;

    /**
     * 查询打款结果
     **/
    @GetMapping("/queryflowingresultall")
    @ResponseBody
    public ResultVO queryFlowingResultAll(@RequestParam(value = "source",required = false)Integer source,@RequestParam(value = "flow",required = false)Long sign){
        settleFlowingService.queryFlowingResult(source,sign);
        return ResultVO.success();
    }

    /**
     * 查询打款结果
     **/
    @LogAnnotion(result = true)
    @GetMapping("/queryflowingresult")
    @ResponseBody
    public ResultVO queryFlowingResult(@RequestParam("source")Integer source,@RequestParam("flow")Long sign){
        SettleFlowing settleFlowing = settleFlowingService.queryFlowingResult(source, sign);
        return settleFlowing==null?ResultVO.error("未查询到结算数据"):ResultVO.success(settleFlowing.getSettleStatus(),settleFlowing.getSettleDesc(),null);
    }

}

