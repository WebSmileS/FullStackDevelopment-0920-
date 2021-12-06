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
import com.yrt.project.api.business.warehouse.service.InspectionRecordService;
import com.yrt.project.api.business.warehouse.vo.ScanBarcodeForm;
import com.yrt.project.api.business.warehouse.vo.UsableProductInfo;
import com.yrt.project.api.business.warehouse.vo.inspection.InspectionRecordForm;
import com.yrt.project.api.business.warehouse.vo.inspection.SearchInspectionRecordForm;
import com.yrt.project.modular.order.domain.PurchasingRecordProduct;
import com.yrt.project.modular.warehouse.domain.InspectionRecord;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/inspection")
@Api(tags = "业务-验货单管理接口")
public class InspectionController extends BaseController {

	@Autowired InspectionRecordService service;
	
	@Log(title = "business.inspection", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增验货单", notes = "新增验货单")
	public ResultInfo<InspectionRecord> insertInspectionRecord(
			@RequestBody @Validated InspectionRecordForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.insertInspectionRecord(form, curUser, false, ""));
	}
	
	@Log(title = "business.inspection", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑验货单", notes = "编辑验货单")
	public ResultInfo<InspectionRecord> updateInspectionRecord(
			@RequestBody @Validated InspectionRecordForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.updateInspectionRecord(form, curUser));
	}
	
	@Log(title = "business.inspection", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "验货单详情", notes = "验货单详情")
	public ResultInfo<InspectionRecord > getInspectionRecordInfo(
			@ApiParam(name="id",value="验货单ID",required=true) @PathVariable Long id) {
		return ResultInfo.success(service.getInspectionRecordInfo(id));
	}
	
	@Log(title = "business.inspection", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除验货单", notes = "删除验货单")
	public AjaxResult deleteInspectionRecord(
			@ApiParam(name="id",value="验货单ID",required=true) @PathVariable Long id) {
		return toAjax(service.deleteInspectionRecord(id));
	}
	
	@Log(title = "business.inspection", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/approval/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "复核验货单", notes = "复核验货单")
	public AjaxResult approvalInspectionRecord(
			@ApiParam(name="id",value="验货单ID",required=true) @PathVariable Long id) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.approvalInspectionRecord(id, curUser));
	}
	
	@Log(title = "business.inspection", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/cancel/{id}", method = RequestMethod.PUT)
	@ApiOperation(value = "作废验货单", notes = "作废验货单")
	public AjaxResult cancelInspectionRecord(
			@ApiParam(name="id",value="验货单ID",required=true) @PathVariable Long id,
			@ApiParam(name="reason",value="作废原因",required=true) @RequestParam String reason) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return toAjax(service.cancelInspectionRecord(id, reason, curUser));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.inspection", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ApiOperation(value = "搜索验货单列表(分页)", notes = "搜索验货单列表(分页)")
	public TableDataInfo<InspectionRecord> searchInspectionRecordList(
			@RequestBody @Validated SearchInspectionRecordForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		List<InspectionRecord> res;
		//加入仓库限制
		List<Long> warehouseIds = getCurUserWarehouseIds();
		startPage();
		if (warehouseIds.size()==0) {
			res = new ArrayList<>();
		}else {
			res = service.searchInspectionRecordList(form, curUser, warehouseIds);
		}
		return getDataTable(res);
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.inspection", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/product/list", method = RequestMethod.POST)
	@ApiOperation(value = "获取可用的采购记录产品列表(分页)", notes = "获取可用的采购记录产品列表(分页)")
	public TableDataInfo<PurchasingRecordProduct> getUsablePurchasingRecordProductList(
			@RequestBody @Validated SearchPurchasingRecordProductForm form) {
		List<PurchasingRecordProduct> res;
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		//加入仓库限制
		List<Long> warehouseIds = getCurUserWarehouseIds();
		if (warehouseIds.size()==0) {
			res = new ArrayList<>();
		}else {
			res = service.getUsablePurchasingRecordProductList(form, curUser, VoucherType.INSPECTION, warehouseIds);
		}
		return getDataTable(res);
	}
	
	@Log(title = "business.inspection", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
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
