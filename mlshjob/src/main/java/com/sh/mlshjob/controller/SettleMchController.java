package com.sh.mlshjob.controller;


import com.sh.mlshcommon.util.DateUtil;
import com.sh.mlshsettlement.common.logaspect.LogAnnotion;
import com.sh.mlshsettlement.service.ISettleMchService;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jinghui.yu
 * @since 2019-11-05
 */
@Controller
@RequestMapping("/api/v1/settlemch")
public class SettleMchController {
    @Resource
    private ISettleMchService settleMchService;

    @ApiOperation(value = "汇总打款数据",httpMethod = "GET")
    @LogAnnotion(result = true)
    @GetMapping("summarymchsettle")
    @ResponseBody
    public void summaryMchSettle(String day){
        LocalDate localDate = DateUtil.parseLocalDate(day, DateUtil.YYYYMMDD);
        settleMchService.summaryMchSettle(localDate);
    }

}

