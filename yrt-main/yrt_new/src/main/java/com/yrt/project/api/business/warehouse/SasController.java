package com.yrt.project.api.business.warehouse;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.*;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.warehouse.service.ISasService;
import com.yrt.project.api.business.warehouse.vo.sas.*;
import com.yrt.project.modular.warehouse.domain.Sas;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1/business/sas")
@Api(tags = "业务-储位分配单管理接口")
public class SasController extends BaseController {

	@Autowired ISasService service;
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.sas", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "搜索储位分配单列表(分页)", notes = "搜索储位分配单列表(分页)")
	public TableDataInfo<Sas> searchSasList(
			@Validated SearchSasForm form) {
		startPage();
		return getDataTable(service.searchSasList(form));
	}
	
	@SuppressWarnings("static-access")
	@Log(title = "business.sas", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	@ApiOperation(value = "获取储位分配单详情(明细排序)", notes = "获取储位分配单详情(明细排序)")
	public ResultInfo<Sas> getSasInfo(
			@Validated SasIDForm form) {
		return new ResultInfo<Sas>().success(service.getSasInfo(form));
	}
	
	@SuppressWarnings("static-access")
	@Log(title = "business.sas", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增储位分配单", notes = "新增储位分配单")
	public ResultInfo<Sas> addSas(
			@RequestBody @Valid AddSasForm form) {
		return new ResultInfo<Sas>().success(service.addSas(form));
	}
	
	@Log(title = "business.sas", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "修改储位分配单", notes = "修改储位分配单")
	public AjaxResult editSas(
			@RequestBody @Valid EditSasForm form) {
		return toAjax(service.editSas(form));
	}
	
	@Log(title = "business.sas", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/stop", method = RequestMethod.PUT)
	@ApiOperation(value = "作业结束", notes = "作业结束")
	public AjaxResult stopSas(
			@Validated SasIDForm form) {
		return toAjax(service.stopSas(form));
	}
	
	@Log(title = "business.sas", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/check", method = RequestMethod.GET)
	@ApiOperation(value = "检测是否存在储位分配单", notes = "检测是否存在储位分配单")
	public AjaxResult checkSas(
			@Validated CheckSasForm form) {
		Sas info = service.checkSas(form);
		if(info == null) {
			return success().put("info", info).put("isNew", true);
		}else {
			return success().put("info", info).put("isNew", false);
		}
	}
	
	@Log(title = "business.sas", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/product/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取可生成储位分配单明细产品信息", notes = "获取可生成储位分配单明细产品信息")
	public AjaxResult getNoHaveSasProductList(
			@Validated SasProductForm form) {
		return success().put("list", service.getNoHaveSasProductList(form));
	}
	
	@Log(title = "business.sas", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除储位分配单", notes = "删除储位分配单")
	public AjaxResult deleteSas(
			@Validated SasIDForm form) {
		return toAjax(service.deleteSas(form));
	}
	
	@Log(title = "business.sas", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/pending/product", method = RequestMethod.GET)
	@ApiOperation(value = "根据储位条码获取待放置货品清单", notes = "根据储位条码获取待放置货品清单")
	public AjaxResult getProductListByWarehouseBarcode(
			@Validated ScanBarcodeForm form) {
		return success().put("info", service.getProductListByWarehouseBarcode(form));
	}
	
	@Log(title = "business.sas", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/pending/storage", method = RequestMethod.GET)
	@ApiOperation(value = "根据产品条码获取待放置储位清单", notes = "根据产品条码获取待放置储位清单")
	public AjaxResult getStorageListByProductBarcode(
			@Validated ScanBarcodeForm form) {
		return success().put("info", service.getStorageListByProductBarcode(form));
	}
	
	@Log(title = "business.sas", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/scan", method = RequestMethod.GET)
	@ApiOperation(value = "根据产品条码获取放置数量", notes = "根据产品条码获取放置数量")
	public AjaxResult getCheckStorageByProductBarcode(
			@Validated({CheckBarcodeForm.Stock.class})CheckBarcodeForm form) {
		return success().put("info", service.getCheckStorageByProductBarcode(form));
	}
	
	@Log(title = "business.sas", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/direct/scan", method = RequestMethod.GET)
	@ApiOperation(value = "根据产品条码获取放置数量(直接放货使用)", notes = "根据产品条码获取放置数量(直接放货使用)")
	public AjaxResult getDirectCheckStorageByProductBarcode(
			@Validated({CheckBarcodeForm.DirectStock.class}) CheckBarcodeForm form) {
		return success().put("info", service.getCheckStorageByProductBarcode(form));
	}
	
	@Log(title = "business.sas", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/stock/product", method = RequestMethod.POST)
	@ApiOperation(value = "放置产品", notes = "放置产品")
	public AjaxResult stockProduct(
			@RequestBody @Validated({StockProductForm.Stock.class}) StockProductForm form) {
		return toAjax(service.stockProduct(form));
	}
	
	@Log(title = "business.sas", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/stock/direct/product", method = RequestMethod.POST)
	@ApiOperation(value = "直接放货", notes = "直接放货")
	public AjaxResult directStockProduct(
			@RequestBody @Validated({StockProductForm.DirectStock.class}) StockProductForm form) {
		return toAjax(service.stockProduct(form));
	}
}
