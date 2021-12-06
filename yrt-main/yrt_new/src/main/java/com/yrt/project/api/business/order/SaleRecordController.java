package com.yrt.project.api.business.order;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
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
import com.yrt.framework.web.domain.ResultList;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.order.service.ISaleRecordService;
import com.yrt.project.api.business.order.vo.record.AddSaleRecordForm;
import com.yrt.project.api.business.order.vo.record.AppSaleRecordSearchForm;
import com.yrt.project.api.business.order.vo.record.EditSaleRecordForm;
import com.yrt.project.api.business.order.vo.record.SaleRecordIDForm;
import com.yrt.project.api.business.order.vo.record.SearchSaleRecordDetailForm;
import com.yrt.project.api.business.order.vo.record.SearchSaleRecordForm;
import com.yrt.project.api.business.order.vo.record.SearchSaleRecordForm2;
import com.yrt.project.api.business.organization.service.IOrgService;
import com.yrt.project.modular.order.domain.SaleRecord;
import com.yrt.project.modular.order.domain.SaleRecordDetail;
import com.yrt.project.modular.order.domain.Sl;
import com.yrt.project.modular.organization.domain.OrgEmployee;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1/business/order/sale/record")
@Api(tags = "业务-销售单管理接口")
public class SaleRecordController extends BaseController {

	@Autowired
	private ISaleRecordService service;
	
	@Autowired
	private IOrgService orgService;
	
	//手机接口
	@SuppressWarnings("unchecked")
	@Log(title = "business.sale.record", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/app/search", method = RequestMethod.GET)
	@ApiOperation(value = "搜索销售单列表(分页，手机专用)", notes = "搜索销售单列表(分页，手机专用)")
	public TableDataInfo<Sl> searchSaleRecordList4Phone(
			@Validated AppSaleRecordSearchForm form) {
		startPage();
		return getDataTable(service.searchSaleRecordList4Phone(form));
	}
	
	//PC和手机通用接口
	@Log(title = "business.sale.record", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "查看销售单列表", notes = "查看销售单列表")
	public ResultList<SaleRecord> selectSaleRecordList(
			@Validated SearchSaleRecordForm form) {
		return ResultList.success(service.selectSaleRecordList(form));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.sale.record", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	@ApiOperation(value = "搜索销售单列表(分页)", notes = "搜索销售单列表(分页)")
	public TableDataInfo<Sl> searchSaleRecordList(
			@Validated SearchSaleRecordForm2 form) {
		startPage();
		return getDataTable(service.searchSaleRecordList(form));
	}
	
	@Log(title = "business.sale.record", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	@ApiOperation(value = "查看销售单详情", notes = "查看销售单详情")
	public ResultInfo<Sl> selectSaleRecordInfo(
			@Validated SaleRecordIDForm form) {
		return ResultInfo.success(service.selectSaleRecordInfo(form));
	}
	
	@Log(title = "business.sale.record", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	@ApiOperation(value = "查看销售单明细列表", notes = "查看销售单明细列表")
	public ResultList<SaleRecordDetail> selectSaleRecordDetailList(
			@Validated SearchSaleRecordDetailForm form) {
		return ResultList.success(service.selectSaleRecordDetailList(form));
	}
	
	@Log(title = "business.sale.record", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "添加销售单", notes = "添加销售单")
	public ResultInfo<Sl> addSaleRecord(
			@RequestBody @Valid AddSaleRecordForm form) {
		ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
		OrgEmployee makePeople = orgService.getEmployeeInfoByUser(ctxUser.getSystemType(), ctxUser.getUid());
		return ResultInfo.success(service.addSaleRecord(makePeople, ctxUser.getSystemType(), form));
	}
	
	@Log(title = "business.sale.record", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "修改销售单信息", notes = "修改销售单信息(只能修改非销售订单生成的销售单)")
	public AjaxResult editSalerecord(
			@RequestBody @Valid EditSaleRecordForm form) {
		return toAjax(service.editSalerecord(form));
	}
	
	@Log(title = "business.sale.record", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除销售单", notes = "删除销售单")
	public AjaxResult deleteSaleRecord(
			@Validated SaleRecordIDForm form) {
		return toAjax(service.deleteSaleRecord(form));
	}
}
