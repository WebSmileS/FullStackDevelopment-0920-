package com.yrt.project.api.business.purchase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.Utils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.AjaxResult;
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.purchase.vo.AddPurchaseReturnForm;
import com.yrt.project.api.business.purchase.vo.EditPurchaseReturnForm;
import com.yrt.project.api.business.sale.vo.SearchReturnProductForm;
import com.yrt.project.api.business.warehouse.vo.grn.GrnIDForm;
import com.yrt.project.api.business.warehouse.vo.grn.GrnIds;
import com.yrt.project.api.business.warehouse.vo.grn.SearchGrnForm;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.warehouse.domain.Grn;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1/business/purchase/return")
@Api(tags = "业务-采购退货接口")
public class PurchaseReturnController extends BaseController {

	@Autowired PurchaseReturnService service;
	
	//TODO: 采购退货 == 负数采购入库
	@SuppressWarnings("unchecked")
	@Log(title = "business.purchase.return", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "搜索采购退货单列表(分页)", notes = "搜索采购退货单列表(分页)")
	public TableDataInfo<Grn> searchPurchaseReturnList(@Validated SearchGrnForm form) {
		List<Grn> res = null;
		//加入仓库限制
		List<Long> warehouseIds = getCurUserWarehouseIds();
		startPage();
		if (warehouseIds.size()==0) {
			res = new ArrayList<>();
		}else {
			res = service.searchPurchaseReturnList(form, warehouseIds);
		}
		return getDataTable(res);
	}
	
	@Log(title = "business.purchase.return", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	@ApiOperation(value = "获取采购退货单详情", notes = "获取采购退货单详情")
	public ResultInfo<Grn> getPurchaseReturnInfo(@Validated GrnIDForm form) {
		return ResultInfo.success(service.selectPurchaseReturnInfo(form));
	}
	
	@Log(title = "business.purchase.return", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增采购退货单", notes = "新增采购退货单")
	public ResultInfo<Grn> insertPurchaseReturn(@RequestBody @Valid AddPurchaseReturnForm form) {
		OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
		return ResultInfo.success(service.insertPurchaseReturn(curUser, ServletUtils.getCurUserSystemType(), form));
	}

	@Log(title = "business.purchase.return", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑采购退货单", notes = "编辑采购退货单")
	public ResultInfo<Grn> updatePurchaseReturn(@RequestBody @Valid EditPurchaseReturnForm form) {
		OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
		return ResultInfo.success(service.updatePurchaseReturn(form, curUser));
	}

	@Log(title = "business.purchase.return", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除采购退货单", notes = "删除采购退货单")
	public AjaxResult deletePurchaseReturn(@Validated GrnIDForm form) {
		OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
		return toAjax(service.deletePurchaseReturn(form, curUser));
	}
	
	@Log(title = "business.purchase.return", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/red", method = RequestMethod.PUT)
	@ApiOperation(value = "采购退货单红冲", notes = "采购退货单红冲")
	public ResultInfo<Grn> purchaseReturnRed(@RequestBody @Valid GrnIDForm form) {
		clearPage();
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.purchaseReturnRed(form, curUser));
	}
	
	@Log(title = "business.purchase.return", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/unreview", method = RequestMethod.PUT)
	@ApiOperation(value = "采购退货单反审核", notes = "采购退货单反审核")
	public AjaxResult unReview(@RequestBody @Valid GrnIDForm form) {
		clearPage();
		service.purchaseReturnUnReview(form);
		return success();
	}
	
	@Log(title = "business.purchase.return", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/putin", method = RequestMethod.PUT)
	@ApiOperation(value = "采购退货入库", notes = "采购退货入库")
	public AjaxResult purchaseReturnPutIn(
			@RequestBody @Valid GrnIds form) {
		clearPage();
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		Map<String,Object> result = service.purchaseReturnPutin(form, curUser);
		if(result == null) {
			return error("数据库没有返回值!");
		}else {
			return success().put("info", result);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.purchase.return", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/product/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取可退货产品列表(分页)", notes = "获取厂商产品列表(分页)")
	public TableDataInfo<Map<String, Object>> getVendorProductList(
			@Validated SearchReturnProductForm form) {
		List<Map<String,Object>> res;
		List<Long> warehouseIds = new ArrayList<>();
		if (Utils.isEmpty(form.getWarehouse_inner_sn())) {
			warehouseIds = getCurUserWarehouseIds();
			if(warehouseIds.isEmpty()) res = new ArrayList<>();
		}else {
			warehouseIds.add(form.getWarehouse_inner_sn());
		}
		startPage();
		res = service.selectProductList(form, warehouseIds);
		return getDataTable(res);
	}
	
}
