package com.yrt.project.api.business.plan;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.yrt.common.dict.SystemType;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.domain.ResultList;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.applyvoucher.vo.AppApplayProductSearchForm;
import com.yrt.project.api.business.plan.service.IPlanService;
import com.yrt.project.api.business.plan.vo.AddPlanForm;
import com.yrt.project.api.business.plan.vo.AppPlanSearchForm;
import com.yrt.project.api.business.plan.vo.BuildOrderForm;
import com.yrt.project.api.business.plan.vo.EditPlanForm;
import com.yrt.project.api.business.plan.vo.IDForm;
import com.yrt.project.api.business.plan.vo.PlanDetailIDForm;
import com.yrt.project.api.business.plan.vo.PlanSearchForm;
import com.yrt.project.api.business.plan.vo.PlanSearchProductForm;
import com.yrt.project.api.business.plan.vo.SearchBatchNumForm;
import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.order.domain.PurchaseOrder;
import com.yrt.project.modular.plan.domain.Plan;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/plan")
@Api(tags = "业务-采购计划接口")
public class PlanController extends BaseController {
	
	@Autowired private IPlanService service;
	
	//手机功能
	@SuppressWarnings("unchecked")
	@Log(title = "business.plan", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/app/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取采购计划列表(分页,手机专用)", notes = "获取采购计划列表(分页,手机专用)")
	public TableDataInfo<Plan> getPlanList4Phone(
			@Validated AppPlanSearchForm form) {
		return getDataTable(service.searchPlanList4Phone(form));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.plan", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/app/product/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取有效合同内产品列表(分页,手机专用)", notes = "获取有效合同内产品列表(分页,手机专用)")
	public TableDataInfo<Map<String, Object>> getContractProductList4Phone(
			@Validated AppApplayProductSearchForm form) {
		startPage();
		if (form.getProduct_type() == 0) {
			return getDataTable(service.selectDealerProductList4Phone(form));
		}
		return getDataTable(service.selectVendorProductList4Phone(form));
	}
	
	//PC和手机共用功能
	@Log(title = "business.plan", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增采购计划", notes = "新增采购计划")
	public ResultInfo<Plan> addInquiry(
			@RequestBody @Valid AddPlanForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		switch (systemType) {
		case DEALER:
			return ResultInfo.success(service.insertDealerPlan(form));
		case HOSPITAL:
			return ResultInfo.success(service.insertHospitalPlan(form));
		default:
		}
		return ResultInfo.error("当前机构不支持这个功能!");
	}
	
	@Log(title = "business.plan", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑采购计划", notes = "编辑采购计划")
	public AjaxResult editInquiry(
			@RequestBody @Valid EditPlanForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		switch (systemType) {
		case DEALER:
			return toAjax(service.updateDealerPlan(form));
		case HOSPITAL:
			return toAjax(service.updateHospitalPlan(form));
		default:
		}
		return error("当前机构不支持这个功能!");
	}
	
	@Log(title = "business.plan", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除采购计划", notes = "删除采购计划")
	public AjaxResult deletePlan(
			@RequestBody @Validated List<IDForm> list) {
		return toAjax(service.deletePlan(list));
	}
	
	@Log(title = "business.plan", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	@ApiOperation(value = "采购计划详情", notes = "采购计划详情")
	public ResultInfo<Plan> getPlanInfo(
			@Validated IDForm form) {
		return ResultInfo.success(service.selectPlanInfo(form));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.plan", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/unexecuted/list/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "未执行采购计划列表(分页)", notes = "未执行采购计划列表(分页)")
	public TableDataInfo<Plan> getUnexecutedPlanList(
			@ApiParam(name="oid",value="当前机构ID",required=true) @PathVariable Long oid,
			@Validated PlanSearchForm form) {
		if(StringUtils.isEmpty(form.getStatus())) form.setStatus("0,1,2");
		return getDataTable(service.searchPlanList(oid, form));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.plan", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/history/list/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "过往采购计划列表(分页)", notes = "过往采购计划列表(分页)")
	public TableDataInfo<Plan> getHistoryPlanList(
			@ApiParam(name="oid",value="当前机构ID",required=true) @PathVariable Long oid,
			@Validated PlanSearchForm form) {
		if(StringUtils.isEmpty(form.getStatus())) form.setStatus("3,4");
		return getDataTable(service.searchPlanList(oid, form));
	}
	
	@Log(title = "business.plan", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/create/{oid}", method = RequestMethod.POST)
	@ApiOperation(value = "计算采购订单数据", notes = "计算采购订单数据")
	public ResultInfo<Map<String, Object>> createOrderData(
			@ApiParam(name="oid",value="当前机构ID",required=true) @PathVariable Long oid,
			@RequestBody @Valid List<IDForm> list) {
		return ResultInfo.success(service.createOrderData(list, oid));
	}
	
	@Log(title = "business.plan", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/build", method = RequestMethod.POST)
	@ApiOperation(value = "生成采购订单", notes = "生成采购订单")
	public ResultList<PurchaseOrder> buildOrder(
			@RequestBody @Valid BuildOrderForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultList.success(service.buildOrder(form, curUser));
	}
	
	@Log(title = "business.plan", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/stop", method = RequestMethod.PUT)
	@ApiOperation(value = "终止计划", notes = "终止计划")
	public AjaxResult stopPlan(
			@RequestBody @Valid List<IDForm> list) {
		return toAjax(service.stopPlan(list));
	}
	
	@Log(title = "business.plan", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/detail/stop", method = RequestMethod.PUT)
	@ApiOperation(value = "终止计划明细", notes = "终止计划明细")
	public AjaxResult stopPlanDetail(
			@Validated PlanDetailIDForm form) {
		return toAjax(service.stopPlanDetail(form));
	}
	
	@Log(title = "business.plan", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/detail/unstop", method = RequestMethod.PUT)
	@ApiOperation(value = "反终止计划明细", notes = "反终止计划明细")
	public AjaxResult unStopPlanDetail(
			@Validated PlanDetailIDForm form) {
		return toAjax(service.unStopPlanDetail(form));
	}
	
	@Log(title = "business.plan", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/detail/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除计划明细", notes = "删除计划明细")
	public AjaxResult deletePlanDetail(
			@Validated PlanDetailIDForm form) {
		return toAjax(service.deletePlanDetail(form));
	}
	
	@Log(title = "business.plan", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/detail/create/{oid}", method = RequestMethod.POST)
	@ApiOperation(value = "勾选计划明细计算采购订单数据", notes = "勾选计划明细计算采购订单数据")
	public ResultInfo<Map<String, Object>> planDetailCreateOrderData(
			@ApiParam(name="oid",value="当前机构ID",required=true) @PathVariable Long oid,
			@RequestBody @Valid List<PlanDetailIDForm> list) {
		return ResultInfo.success(service.planDetailCreateOrderData(list,oid));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.plan", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/vendor/list/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "厂商产品列表(分页)", notes = "厂商产品列表(分页)")
	public TableDataInfo<ResponseProductInfo> getVendorProductList(
			@ApiParam(name="oid",value="当前机构ID",required=true) @PathVariable Long oid,
			PlanSearchProductForm form) {
		startPage();
		return getDataTable(service.selectVendorProductList(oid, form));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.plan", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/dealer/list/{oid}", method = RequestMethod.GET)
	@ApiOperation(value = "经销商自建产品列表(分页)", notes = "经销商自建产品列表(分页)")
	public TableDataInfo<ResponseProductInfo> getDealerProductList(
			@ApiParam(name="oid",value="当前机构ID",required=true) @PathVariable Long oid,
			PlanSearchProductForm form) {
		startPage();
		return getDataTable(service.selectDealerProductList(oid, form));
	}
	
	@Log(title = "business.plan", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/batch/list", method = RequestMethod.POST)
	@ApiOperation(value = "获取仓库内产品批号列表", notes = "获取仓库内产品批号列表")
	public AjaxResult getProductBatchNumList(@Validated @RequestBody SearchBatchNumForm form) {
		return success().put("list", service.selectProductBatchNumList(form));
	}
}
