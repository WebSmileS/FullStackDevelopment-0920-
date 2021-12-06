package com.yrt.project.api.business.warehouse;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.yrt.framework.web.domain.ResultList;
import com.yrt.project.api.business.warehouse.service.impl.InventoryServiceImpl;
import com.yrt.project.api.business.warehouse.vo.WarehouseSearchProductForm;
import com.yrt.project.modular.warehouse.domain.InventoryProduct;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/inventory")
@Api(tags = "业务-库存产品管理接口")
public class InventoryController extends BaseController {

	@Autowired private InventoryServiceImpl service;
	
	//临时接口
	@Log(title = "business.warehouse", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/first/{org_id}", method = RequestMethod.GET)
	@ApiOperation(value = "首次进入查询库存产品页面", notes = "首次进入查询库存产品页面")
	public AjaxResult getFirstProductName(
			@ApiParam(name="org_id",value="当前机构ID",required=true) @PathVariable Long org_id) {
		return success().put("info", service.getFirstProductName(org_id, ServletUtils.getCurUserSystemType()));
	}
	
	@Log(title = "business.warehouse", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list/{level}", method = RequestMethod.GET)
	@ApiOperation(value = "按级别查询库存产品", notes = "按级别查询库存产品")
	public ResultList<InventoryProduct> getTopLevelWarehouseList(
			@ApiParam(name="level",value="查询级别(1-3级)",required=true) @PathVariable Integer level,
			@Validated WarehouseSearchProductForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		List<Long> warehouseIds = getCurUserWarehouseIds();
		if (warehouseIds.isEmpty()) return ResultList.success(new ArrayList<>());
		return ResultList.success(service.selectInventoryByLevel(level, form, curUser.getSystemType(), warehouseIds));
	}

	@Log(title = "business.warehouse", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/global/list/{level}", method = RequestMethod.GET)
	@ApiOperation(value = "按级别查询库存产品，查全部仓库库存", notes = "按级别查询库存产品，类似用于全局管理员查询")
	public ResultList<InventoryProduct> getTopLevelWarehouseListGlobal(
			@ApiParam(name="level",value="查询级别(1-3级)",required=true) @PathVariable Integer level,
			@Validated WarehouseSearchProductForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultList.success(service.selectInventoryByLevel(level, form, curUser.getSystemType(), new ArrayList<>()));
	}
	
	/* 取消储位功能，故取消条码相关功能
	 * @Log(title = "business.warehouse", businessType = BusinessType.OTHER,
	 * operatorType = OperatorType.BUSINESS)
	 * 
	 * @RequestMapping(value = "/barcode/scan", method = RequestMethod.POST)
	 * 
	 * @ApiOperation(value = "扫描条码获取产品信息", notes = "扫描条码获取产品信息") public AjaxResult
	 * scanBarcode(
	 * 
	 * @Validated ScanBarcodeForm form) { return success().put("info",
	 * service.scanBarcode(form)); }
	 * 
	 * @SuppressWarnings("unchecked")
	 * 
	 * @Log(title = "business.warehouse", businessType = BusinessType.OTHER,
	 * operatorType = OperatorType.BUSINESS)
	 * 
	 * @RequestMapping(value = "/app/unplace/list", method = RequestMethod.GET)
	 * 
	 * @ApiOperation(value = "查询未放置的库存产品列表(分页,手机专用)", notes =
	 * "查询未放置的库存产品列表(分页,手机专用)") public TableDataInfo<InventoryProduct4Phone>
	 * getUnPlaceInventoryProduct4Phone(
	 * 
	 * @Validated SearchUnPlaceProductForm form) { startPage(); return
	 * getDataTable(service.getUnPlaceInventoryProduct4Phone(form)); }
	 */
}
