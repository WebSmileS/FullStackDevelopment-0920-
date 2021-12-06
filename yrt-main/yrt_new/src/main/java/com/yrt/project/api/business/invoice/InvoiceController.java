package com.yrt.project.api.business.invoice;

import com.yrt.common.dict.SystemInvoiceType;
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
import com.yrt.project.api.business.invoice.vo.*;
import com.yrt.project.modular.invoice.domain.SystemInvoice;
import com.yrt.project.modular.invoice.domain.SystemInvoiceDetail;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/business/invoice")
@Api(tags = "业务-发票管理接口")
public class InvoiceController extends BaseController {

	@Autowired InvoiceService service;
	
	@Log(title = "business.invoice", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/grn/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增入库发票", notes = "新增入库发票")
	public ResultInfo<SystemInvoice> insertGrnInvoice(
			@RequestBody @Validated AddInvoiceForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.insertInvoice(form, SystemInvoiceType.GRN_INVOICE, curUser));
	}
	
	@Log(title = "business.invoice", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/odo/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增出库发票", notes = "新增出库发票")
	public ResultInfo<SystemInvoice> insertOdoInvoice(
			@RequestBody @Validated AddInvoiceForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.insertInvoice(form, SystemInvoiceType.ODO_INVOICE, curUser));
	}
	
	@Log(title = "business.invoice", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/grn/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑入库发票", notes = "编辑入库发票")
	public ResultInfo<SystemInvoice> updateGrnInvoice(
			@RequestBody @Validated EditGrnInvoiceForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.updateGrnInvoice(form, SystemInvoiceType.GRN_INVOICE, curUser));
	}
	
	@Log(title = "business.invoice", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/odo/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑出库发票", notes = "编辑出库发票")
	public ResultInfo<SystemInvoice> updateOdoInvoice(
			@RequestBody @Validated EditOdoInvoiceForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.updateOdoInvoice(form, SystemInvoiceType.ODO_INVOICE, curUser));
	}
	
	@Log(title = "business.invoice", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/grn/submit", method = RequestMethod.PUT)
	@ApiOperation(value = "送审入库发票", notes = "送审入库发票")
	public AjaxResult submitGrnInvoice(
			@RequestBody @Validated List<Long> list) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.submitInvoice(list, SystemInvoiceType.GRN_INVOICE, curUser));
	}
	
	@Log(title = "business.invoice", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/odo/submit", method = RequestMethod.PUT)
	@ApiOperation(value = "送审出库发票", notes = "送审出库发票")
	public AjaxResult submitOdoInvoice(
			@RequestBody @Validated List<Long> list) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.submitInvoice(list, SystemInvoiceType.ODO_INVOICE, curUser));
	}
	
	@Log(title = "business.invoice", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除出入库发票", notes = "删除出入库发票")
	public AjaxResult deleteGrn(
			@RequestBody List<Long> list) {
		return toAjax(service.deleteInvoice(list));
	}
	
	@Log(title = "business.invoice", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "获取出入库发票详情", notes = "获取出入库发票详情")
	public ResultInfo<SystemInvoice> selectGrnInvoiceInfo(
			@ApiParam(name="id",value="发票ID",required=true) @PathVariable Long id) {
		return ResultInfo.success(service.selectInvoiceInfo(id));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.invoice", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/grn/list", method = RequestMethod.POST)
	@ApiOperation(value = "搜索入库发票列表(分页)", notes = "搜索入库发票列表(分页)")
	public TableDataInfo<SystemInvoice> searchGrnInvoiceList(
			@RequestBody  @Validated SearchInvoiceForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(service.searchInvoiceList(form, curUser, SystemInvoiceType.GRN_INVOICE));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.invoice", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/odo/list", method = RequestMethod.POST)
	@ApiOperation(value = "搜索出库发票列表(分页)", notes = "搜索出库发票列表(分页)")
	public TableDataInfo<SystemInvoice> searchOdoInvoiceList(
			@RequestBody @Validated SearchInvoiceForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(service.searchInvoiceList(form, curUser, SystemInvoiceType.ODO_INVOICE));
	}
	
	@Log(title = "business.invoice", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/detail/list/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "搜索发票明细列表", notes = "搜索发票明细列表")
	public ResultList<SystemInvoiceDetail> getInvoiceDetailList(
			@ApiParam(name="id",value="发票ID",required=true) @PathVariable Long id) {
		return ResultList.success(service.getInvoiceDetailList(id));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.purchase.return", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/grn/product/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取入库发票产品列表(分页)", notes = "获取可做入库发票的产品列表(分页)")
	public TableDataInfo<Map<String, Object>> getGrnProductList(
			@Validated SearchInvoiceProductForm form) {
		List<Map<String,Object>> res;
		List<Long> warehouseIds = getCurUserWarehouseIds();
		if(warehouseIds.isEmpty()) res = new ArrayList<>();
		
		startPage();
		res = service.selectProductList(form, warehouseIds, SystemInvoiceType.GRN_INVOICE);
		return getDataTable(res);
	}

	@Log(title = "business.purchase.return", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/grn/product/price/count", method = RequestMethod.PUT)
	@ApiOperation(value = "获取入库待开发票总金额", notes = "获取入库待开发票总金额")
	public ResultInfo<String> getgrnProductPriceCount(
			@RequestBody @Validated SearchInvoiceProductForm form) {
		List<Long> warehouseIds = getCurUserWarehouseIds();
		List<Map<String,Object>> res = service.selectProductList(form, warehouseIds, SystemInvoiceType.GRN_INVOICE);
		// 总价格
		BigDecimal sumPrice = BigDecimal.ZERO;
		for (Map<String, Object> re : res) {
			BigDecimal unitPrice = re.get("unit_price")==null?BigDecimal.ZERO:new BigDecimal(re.get("unit_price").toString());
			BigDecimal quantity = re.get("quantity")==null?BigDecimal.ZERO:new BigDecimal(re.get("quantity").toString());
			sumPrice = unitPrice.multiply(quantity).add(sumPrice).setScale(4, BigDecimal.ROUND_HALF_UP);
		}
		return ResultInfo.success(sumPrice.toString());
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.purchase.return", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/odo/product/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取出库发票产品列表(分页)", notes = "获取可做出库发票的产品列表(分页)")
	public TableDataInfo<Map<String, Object>> getOdoProductList(
			@Validated SearchInvoiceProductForm form) {
		List<Map<String,Object>> res;
		List<Long> warehouseIds = getCurUserWarehouseIds();
		if(warehouseIds.isEmpty()) res = new ArrayList<>();
		
		startPage();
		res = service.selectProductList(form, warehouseIds, SystemInvoiceType.ODO_INVOICE);
		return getDataTable(res);
	}


	@Log(title = "business.purchase.return", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/odo/product/price/count", method = RequestMethod.PUT)
	@ApiOperation(value = "获取出库待开发票总金额", notes = "获取出库待开发票总金额")
	public ResultInfo<String> getOdoProductPriceCount(
			@RequestBody @Validated SearchInvoiceProductForm form) {
		List<Long> warehouseIds = getCurUserWarehouseIds();
		List<Map<String,Object>> res = service.selectProductList(form, warehouseIds, SystemInvoiceType.ODO_INVOICE);
		// 总价格
		BigDecimal sumPrice = BigDecimal.ZERO;
		for (Map<String, Object> re : res) {
			BigDecimal unitPrice = re.get("unit_price")==null?BigDecimal.ZERO:new BigDecimal(re.get("unit_price").toString());
			BigDecimal quantity = re.get("quantity")==null?BigDecimal.ZERO:new BigDecimal(re.get("quantity").toString());
			sumPrice = unitPrice.multiply(quantity).add(sumPrice).setScale(4,BigDecimal.ROUND_HALF_UP);
		}
		return ResultInfo.success(sumPrice.toString());
	}
}
