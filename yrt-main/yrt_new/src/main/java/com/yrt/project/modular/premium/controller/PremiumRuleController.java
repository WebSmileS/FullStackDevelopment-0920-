package com.yrt.project.modular.premium.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.project.modular.advance.dto.GetAdvanceSaleDtoResp;
import com.yrt.project.modular.premium.service.PremiumRuleService;
import com.yrt.project.modular.premium.domain.PremiumRule;
import com.yrt.project.modular.premium.dto.PremiumRuleDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/premium/rule")
@Api(tags = "业务-溢价规则管理接口")
@RequiredArgsConstructor
@Slf4j
public class PremiumRuleController extends BaseController {

    private final PremiumRuleService service;

    @Log(title = "business.premium", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiOperation(value = "分页查询溢价规则列表", notes = "分页查询溢价规则列表")
    public IPage<GetAdvanceSaleDtoResp> getAdvanceSaleList(Page page) {
        ContextUserInfo curUser = ServletUtils.getCurUserInfo();
        QueryWrapper qw = new QueryWrapper();
        qw.eq(PremiumRule.COL_ORG_ID, curUser.getOrgId());
        return service.page(page, qw);
    }

    @Log(title = "business.premium", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "删除溢价规则", notes = "删除溢价规则")
    public ResultInfo deleteAdvanceSaleList(@ApiParam(name="id",value="溢价规则ID",required=true) @PathVariable("id") Long id) {
        service.removeById(id);
        return ResultInfo.success(null);
    }

    @Log(title = "business.premium", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/addOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "新增溢价规则/修改溢价规则", notes = "新增预售单/修改预售单, 如果是修改预售单需要传原预售单ID")
    public ResultInfo addOrUpdate(@RequestBody @Validated PremiumRuleDto reqDto) {
        ContextUserInfo curUser = ServletUtils.getCurUserInfo();
        QueryWrapper<PremiumRule> queryWrapper = new QueryWrapper();
        queryWrapper.eq(PremiumRule.COL_ORG_ID, curUser.getOrgId());
        List<PremiumRule> premiumRules = service.list(queryWrapper);
        if (reqDto.getMinPrice().compareTo(reqDto.getMaxPrice()) == 1){
            throw new RuntimeException("最小值不能大于最大值");
        }
        try {
            if (reqDto.getId() == null) {
                validatePrice(reqDto, premiumRules);

                // 新增
                PremiumRule rule = new PremiumRule();
                BeanUtils.copyProperties(reqDto, rule);
                rule.setOrgId(curUser.getOrgId());
                rule.setOrgName("");
                rule.setUpdateUserId(curUser.getUid());
                rule.setUpdateUserName("");
                service.save(rule);
            } else {
                // 修改，过滤掉自己的数据与其他数据比较
                premiumRules = premiumRules.stream().filter(e -> !e.getId().equals(reqDto.getId())).collect(Collectors.toList());
                validatePrice(reqDto, premiumRules);
                PremiumRule rule = service.getById(reqDto.getId());
                BeanUtils.copyProperties(reqDto, rule);
                rule.setOrgId(curUser.getOrgId());
                rule.setOrgName("");
                rule.setUpdateUserId(curUser.getUid());
                rule.setUpdateUserName("");
                service.updateById(rule);
            }
        }catch (Exception exception){
            log.error("新增溢价规则/修改溢价规则异常", exception);
            return ResultInfo.error(exception.getMessage());
        }
        return ResultInfo.success(null);
    }

    private void validatePrice(PremiumRuleDto reqDto, List<PremiumRule> premiumRules) {
        for (PremiumRule premiumRule : premiumRules) {
            if (premiumRule.getMinPrice().compareTo(reqDto.getMinPrice())< 1
                    && premiumRule.getMaxPrice().compareTo(reqDto.getMinPrice()) > -1){
                // 重复区间
                String msg = String.format("最小值[%s]与区间[%s-%s]冲突", reqDto.getMinPrice().toString(),
                        premiumRule.getMinPrice().toString(),premiumRule.getMaxPrice().toString());
                throw new RuntimeException(msg);
            }
            if (premiumRule.getMinPrice().compareTo(reqDto.getMaxPrice())< 1
                    && premiumRule.getMaxPrice().compareTo(reqDto.getMaxPrice()) > -1){
                // 重复区间
                String msg = String.format("最大值[%s]与区间[%s-%s]冲突", reqDto.getMaxPrice().toString(),
                        premiumRule.getMinPrice().toString(),premiumRule.getMaxPrice().toString());
                throw new RuntimeException(msg);
            }
        }
    }

}