package com.yrt.project.api.business.sale;

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
import com.yrt.project.api.business.sale.vo.AddSaleReturnForm;
import com.yrt.project.api.business.sale.vo.EditSaleReturnForm;
import com.yrt.project.api.business.sale.vo.SearchReturnProductForm;
import com.yrt.project.api.business.warehouse.vo.odo.OdoIDForm;
import com.yrt.project.api.business.warehouse.vo.odo.OdoListIDForm;
import com.yrt.project.api.business.warehouse.vo.odo.SearchOdoForm;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.warehouse.domain.Odo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 销售退货由于调单时成本价不可编辑，且来自于销售单，是已经经过了单位换算的，所以本处不需要单位换算。
 *
 *
 */
@RestController
@RequestMapping("/v1/business/sale/return")
@Api(tags = "业务-销售退货接口")
public class SaleReturnController extends BaseController {

	@Autowired SaleReturnService returnService;
	
	//TODO: 销售退货==负数销售出库
	@Log(title = "business.sale.return", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增销售退货单", notes = "新增销售退货单")
	public ResultInfo<Odo> insertSaleReturn(
			@RequestBody @Validated AddSaleReturnForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(returnService.insertSaleReturn(form, curUser.getSystemType(), curUser.getEmployee()));
	}
	
	@Log(title = "business.sale.return", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑销售退货单", notes = "编辑销售退货单")
	public ResultInfo<Odo> updateSaleReturn(
			@RequestBody @Validated EditSaleReturnForm form) {
		OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
		return ResultInfo.success(returnService.updateSaleReturn(form, curUser));
	}
	
	@Log(title = "business.sale.return", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除销售退货单", notes = "删除销售退货单")
	public AjaxResult deleteSaleReturn(
			@Validated OdoIDForm form) {
		return toAjax(returnService.deleteSaleReturn(form));
	}
	
	@Log(title = "business.sale.return", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/putout", method = RequestMethod.PUT)
	@ApiOperation(value = "销售退货单入库", notes = "销售退货单入库")
	public AjaxResult saleReturnPutOut(@RequestBody @Valid OdoListIDForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		String msg = returnService.saleReturnPutout(form, curUser);
		return success(msg);
	}
	
	@Log(title = "business.sale.return", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	@ApiOperation(value = "获取销售退货单详情", notes = "获取销售退货单详情")
	public ResultInfo<Odo> selectSaleReturnInfo(
			@Validated OdoIDForm form) {
		return ResultInfo.success(returnService.selectSaleReturnInfo(form));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.sale.return", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "搜索销售退货单列表(分页)", notes = "搜索销售退货单列表(分页)")
	public TableDataInfo<Odo> searchGrnList(
			@Validated SearchOdoForm form) {
		List<Odo> res = null;
		//加入仓库限制
		List<Long> warehouseIds = getCurUserWarehouseIds();
		startPage();
		if (warehouseIds.size()==0) {
			res = new ArrayList<>();
		}else {
			res = returnService.searchSaleReturnList(form, warehouseIds);
		}
		return getDataTable(res);
	}
	
	@Log(title = "business.sale.return", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/red", method = RequestMethod.PUT)
	@ApiOperation(value = "销售退货单红冲", notes = "销售退货单红冲")
	public ResultInfo<Odo> saleReturnRed(@RequestBody @Valid OdoIDForm form) {
		clearPage();
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(returnService.saleReturnRed(form, curUser));
	}
	
	@Log(title = "business.sale.return", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/unreview", method = RequestMethod.PUT)
	@ApiOperation(value = "销售退货单反审核", notes = "销售退货单反审核")
	public AjaxResult saleReturnUnReview(@RequestBody @Valid OdoIDForm form) {
		clearPage();
		returnService.saleReturnUnReview(form);
		return success();
	}
	
	@Log(title = "business.sale.return", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/printed", method = RequestMethod.PUT)
	@ApiOperation(value = "销售退货单打印完成", notes = "销售退货单打印完成")
	public AjaxResult printed(
			@Validated OdoIDForm form) {
		returnService.printed(form);
		return success();
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/vendor/list", method = RequestMethod.GET)
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
		res = returnService.selectProductList(form, warehouseIds);
		return getDataTable(res);
	}
}
