package com.yrt.project.api.business.invoice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.invoice.vo.AddAdvancePaymentForm;
import com.yrt.project.api.business.invoice.vo.EditAdvancePaymentForm;
import com.yrt.project.api.business.invoice.vo.SearchInvoiceForm;
import com.yrt.project.modular.invoice.domain.AdvancePayment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/advance")
@Api(tags = "业务-预付款管理接口")
public class AdvancePaymentController extends BaseController {

	@Autowired AdvancePaymentService service;
	
	@Log(title = "business.advance", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增预付款", notes = "新增预付款")
	public ResultInfo<AdvancePayment> insertAdvancePayment(
			@RequestBody @Validated AddAdvancePaymentForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.insertAdvancePayment(form, curUser));
	}
	
	@Log(title = "business.advance", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑预付款", notes = "编辑预付款")
	public ResultInfo<AdvancePayment> updateAdvancePayment(
			@RequestBody @Validated EditAdvancePaymentForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.updateAdvancePayment(form, curUser));
	}
	
	@Log(title = "business.advance", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/submit", method = RequestMethod.PUT)
	@ApiOperation(value = "送审预付款", notes = "送审预付款")
	public AjaxResult submitAdvancePayment(
			@RequestBody @Validated List<Long> list) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.submitAdvancePayment(list, curUser));
	}
	
	@Log(title = "business.advance", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除预付款", notes = "删除预付款")
	public AjaxResult deleteGrn(
			@RequestBody List<Long> list) {
		return toAjax(service.deleteAdvancePayment(list));
	}
	
	@Log(title = "business.advance", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取预付款详情", notes = "获取预付款详情")
	public ResultInfo<AdvancePayment> selectAdvancePaymentInfo(
			@ApiParam(name="id",value="预付款ID",required=true) @PathVariable Long id) {
		return ResultInfo.success(service.selectAdvancePaymentInfo(id));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.advance", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ApiOperation(value = "搜索预付款列表(分页)", notes = "搜索预付款列表(分页)")
	public TableDataInfo<AdvancePayment> searchAdvancePaymentList(
			@RequestBody  @Validated SearchInvoiceForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(service.searchAdvancePaymentList(form, curUser));
	}
}
