package com.yrt.project.api.business.surgery.plan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.yrt.common.lang.KeyValue;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.ResultList;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.framework.web.page.TableSupport;
import com.yrt.project.api.business.surgery.cust.service.SurgeryCustService;
import com.yrt.project.api.business.surgery.cust.vo.CustPackageCondition;
import com.yrt.project.api.business.surgery.plan.service.OperationPlanSupportService;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustOis;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/surgery/plan/support")
@Api(tags = "业务-骨科手术plan(Support)")
public class OperationPlanSupportController extends BaseController {
	
	@Autowired
	private OperationPlanSupportService service;
	
	@Autowired
	private SurgeryCustService packageService;
	
	@GetMapping({"/brand/availableForPlan/{hospital_inner_sn}", "/brand/availableForPlan/{hospital_inner_sn}/{op_inner_sn}"})
	@ApiOperation("1. 计划可选品牌(意向内: hospital/[op_inner_sn]) ")
	@ApiOperationSupport(order=1)
	public ResultList<KeyValue<Long, String>> listAvailableBrandForPlan(
			@ApiParam(value="医院id", required=true)@PathVariable("hospital_inner_sn")Long hospital_inner_sn,
			@ApiParam(value="路径id", required=false)@PathVariable(name="op_inner_sn", required=false)Long op_inner_sn
			) {
		return ResultList.success(service.getAvailableBrandForPlan(hospital_inner_sn, op_inner_sn));
	}
	
	@GetMapping({"/type/availableForPlan/{hospital_inner_sn}", "type/availableForPlan/{hospital_inner_sn}/{op_inner_sn}"})
	@ApiOperation("2. 计划可选 手术类型(意向内: hospital/[op_inner_sn]) ")
	@ApiOperationSupport(order=2)
	public ResultList<KeyValue<Long, String>> listAvailableTypeForPlan(
			@ApiParam(value="医院id", required=true)@PathVariable("hospital_inner_sn")Long hospital_inner_sn,
			@ApiParam(value="路径id", required=false)@PathVariable(name="op_inner_sn", required=false)Long op_inner_sn
			) {
		return ResultList.success(service.getAvailableTypeForPlan(hospital_inner_sn, op_inner_sn));
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping({"/package/list/{hospital_inner_sn}", "/package/list/{hospital_inner_sn}/{dealer_inner_sn}"})
	@ApiOperation("20.list计划可选手术包(分页: hospital/[dealer]) ")
	@ApiOperationSupport(order=20)
	public TableDataInfo<OrthopaedicCustOis> listPackageForPlan(
			@ApiParam(value="医院id", required=true)@PathVariable("hospital_inner_sn")Long hospital_inner_sn,
			@ApiParam(value="经销商id", required=false)@PathVariable(name="dealer_inner_sn", required=false)Long dealer_inner_sn,
			@RequestBody @Validated CustPackageCondition condition) {
		//startPage();
		return getDataTable(packageService.listPackageForPlan(hospital_inner_sn, dealer_inner_sn, condition, TableSupport.buildValidPageRequest()));
	}
}
