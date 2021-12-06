package com.yrt.project.api.business.warehouse;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.dict.VoucherType;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.order.vo.purchasingRecord.SearchPurchasingRecordProductForm;
import com.yrt.project.api.business.warehouse.service.ArrivalRecordService;
import com.yrt.project.api.business.warehouse.vo.ScanBarcodeForm;
import com.yrt.project.api.business.warehouse.vo.UsableProductInfo;
import com.yrt.project.api.business.warehouse.vo.receipt.ArrivalRecordForm;
import com.yrt.project.api.business.warehouse.vo.receipt.SearchArrivalRecordForm;
import com.yrt.project.modular.order.domain.PurchasingRecordProduct;
import com.yrt.project.modular.warehouse.domain.ArrivalRecord;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/arrival")
@Api(tags = "业务-收货单管理接口")
public class ArrivalRecordController extends BaseController {

	@Autowired ArrivalRecordService service;
	
	@Log(title = "business.arrival", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增收货单", notes = "新增收货单")
	public ResultInfo<ArrivalRecord> insertArrivalRecord(
			@RequestBody @Validated ArrivalRecordForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.insertArrivalRecord(form, curUser, false, ""));
	}
	
	@Log(title = "business.arrival", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑收货单", notes = "编辑收货单")
	public ResultInfo<ArrivalRecord> updateArrivalRecord(
			@RequestBody @Validated ArrivalRecordForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.updateArrivalRecord(form, curUser));
	}
	
	@Log(title = "business.arrival", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "收货单详情", notes = "收货单详情")
	public ResultInfo<ArrivalRecord > getArrivalRecordInfo(
			@ApiParam(name="id",value="收货单ID",required=true) @PathVariable Long id) {
		return ResultInfo.success(service.getArrivalRecordInfo(id));
	}
	
	@Log(title = "business.arrival", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除收货单", notes = "删除收货单")
	public AjaxResult deleteArrivalRecord(
			@ApiParam(name="id",value="收货单ID",required=true) @PathVariable Long id) {
		return toAjax(service.deleteArrivalRecord(id));
	}
	
	@Log(title = "business.arrival", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/approval/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "复核收货单", notes = "复核收货单")
	public AjaxResult approvalArrivalRecord(
			@ApiParam(name="id",value="收货单ID",required=true) @PathVariable Long id) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.approvalArrivalRecord(id, curUser));
	}
	
	@Log(title = "business.arrival", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/cancel/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "作废收货单", notes = "作废收货单")
	public AjaxResult cancelArrivalRecord(
			@ApiParam(name="id",value="收货单ID",required=true) @PathVariable Long id,
			@ApiParam(name="reason",value="作废原因",required=true) @RequestParam String reason) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.cancelArrivalRecord(id, reason, curUser));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.arrival", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ApiOperation(value = "搜索收货单列表(分页)", notes = "搜索收货单列表(分页)")
	public TableDataInfo<ArrivalRecord> searchArrivalRecordList(
			@RequestBody @Validated SearchArrivalRecordForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		List<ArrivalRecord> res;
		//加入仓库限制
		List<Long> warehouseIds = getCurUserWarehouseIds();
		startPage();
		if (warehouseIds.size()==0) {
			res = new ArrayList<>();
		}else {
			res = service.searchArrivalRecordList(form, curUser, warehouseIds);
		}
		return getDataTable(res);
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.arrival", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/product/list", method = RequestMethod.POST)
	@ApiOperation(value = "获取可用的采购记录产品列表(分页)", notes = "获取可用的采购记录产品列表(分页)")
	public TableDataInfo<PurchasingRecordProduct> getGrnProductList(
			@RequestBody @Validated SearchPurchasingRecordProductForm form) {
		List<PurchasingRecordProduct> res;
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		//加入仓库限制
		List<Long> warehouseIds = getCurUserWarehouseIds();
		if (warehouseIds.size()==0) {
			res = new ArrayList<>();
		}else {
			res = service.getUsablePurchasingRecordProductList(form, curUser, VoucherType.ARRIVAL_RECORD, warehouseIds);
		}
		return getDataTable(res);
	}
	
	@Log(title = "business.arrival", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/scan", method = RequestMethod.POST)
	@ApiOperation(value = "扫码获取产品信息", notes = "获取可用的采购记录产品列表(分页)")
	public ResultInfo<UsableProductInfo> scanProductByBarcode(
			@RequestBody @Validated ScanBarcodeForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		//加入仓库限制
		UsableProductInfo res = new UsableProductInfo ();
		List<Long> warehouseIds = getCurUserWarehouseIds();
		if (warehouseIds.size()!=0) {
			res = service.scanProductByBarcode(form, curUser, warehouseIds);
		}
		return ResultInfo.success(res);
	}
}
