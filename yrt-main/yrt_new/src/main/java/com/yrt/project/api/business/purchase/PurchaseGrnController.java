package com.yrt.project.api.business.purchase;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.RefVoucherType;
import com.yrt.common.exception.DBException;
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
import com.yrt.project.api.business.purchase.vo.AddPurchaseReturnForm;
import com.yrt.project.api.business.purchase.vo.EditPurchaseReturnForm;
import com.yrt.project.api.business.warehouse.vo.grn.GrnIDForm;
import com.yrt.project.api.business.warehouse.vo.grn.SearchGrnForm;
import com.yrt.project.api.platform.service.SysParameterService;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.warehouse.domain.Grn;
import com.yrt.project.modular.warehouse.domain.TransferVoucher;
import com.yrt.project.modular.warehouse.domain.TransferVoucherDetail;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/purchase/grn")
@Api(tags = "业务-采购入库接口")
public class PurchaseGrnController extends BaseController {

	@Autowired PurchaseGrnService service;
	@Autowired SysParameterService paramService;
	
	//TODO: 采购入库==原采购入库
	@Log(title = "business.purchase.grn", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增采购入库单", notes = "新增采购入库单")
	public ResultInfo<Grn> insertPurchaseGrn(
			@RequestBody @Validated AddPurchaseReturnForm form) {
		OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
		return ResultInfo.success(service.insertPurchaseGrn(form, curUser));
	}
	
	@Log(title = "business.purchase.grn", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑采购入库单", notes = "编辑采购入库单")
	public ResultInfo<Grn> updatePurchaseGrn(
			@RequestBody @Validated EditPurchaseReturnForm form) {
		OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
		return ResultInfo.success(service.updatePurchaseGrn(form, curUser));
	}
	
	@Log(title = "business.purchase.grn", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除采购入库单", notes = "删除采购入库单")
	public AjaxResult deleteGrn(
			@Validated GrnIDForm form) {
		OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
		return toAjax(service.deletePurchaseGrn(form, curUser));
	}
	
	@Log(title = "business.purchase.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	@ApiOperation(value = "获取采购入库单详情", notes = "获取采购入库单详情")
	public ResultInfo<Grn> selectPurchaseGrnInfo(
			@Validated GrnIDForm form) {
		return ResultInfo.success(service.selectPurchaseGrnInfo(form));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.purchase.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "搜索采购入库单列表(分页)", notes = "搜索采购入库单列表(分页)")
	public TableDataInfo<Grn> searchGrnList(
			@Validated SearchGrnForm form) {
		List<Grn> res = null;
		//加入仓库限制
		List<Long> warehouseIds = getCurUserWarehouseIds();
		startPage();
		if (warehouseIds.size()==0) {
			res = new ArrayList<>();
		}else {
			res = service.searchPurchaseGrnList(form, warehouseIds);
		}
		return getDataTable(res);
	}
	
	@Log(title = "business.purchase.grn", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/red", method = RequestMethod.PUT)
	@ApiOperation(value = "采购入库单红冲", notes = "采购入库单红冲")
	public ResultInfo<Grn> purchaseGrnRed(@RequestBody @Valid GrnIDForm form) {
		clearPage();
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.purchaseGrnRed(form, curUser));
	}
	
	@Log(title = "business.purchase.grn", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/unreview", method = RequestMethod.PUT)
	@ApiOperation(value = "采购入库单反审核", notes = "采购入库单反审核")
	public AjaxResult purchaseGrnUnReview(@RequestBody @Valid GrnIDForm form) {
		clearPage();
		service.purchaseGrnUnReview(form);
		return success();
	}
	
	@Log(title = "business.purchase.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/sheet/list", method = RequestMethod.GET)
	@ApiOperation(value = "采购入库单可调单类型列表", notes = "采购入库单可调单类型列表")
	public ResultList<RefVoucherType> getOrderSheetList() {
		List<RefVoucherType> list = new ArrayList<>();
		ContextUserInfo info = ServletUtils.getCurUserInfo();
		switch (info.getSystemType()) {
		case HOSPITAL:
		case DEALER:
			if(paramService.isSingle(info.getSystemType(), info.getOrgId())) {
				list.add(RefVoucherType.ORDER_PO);
			}else {
				list.add(RefVoucherType.ODO);
			}
			break;
		case VENDOR:
			throw new DBException("当前机构不支持该功能!");
		default:
		}
		return ResultList.success(list);
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.purchase.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/transfer/record/list", method = RequestMethod.GET)
	@ApiOperation(value = "可用做采购入库调单的单据列表", notes = "可用做采购入库调单的单据列表")
	public TableDataInfo<TransferVoucher> getTransferOdoList(
			@ApiParam(name="voucherType",value="调单类型",required=true) @RequestParam(required=true) RefVoucherType voucherType,
			@ApiParam(name="sn",value="搜索销售单单号",required=false) String sn,
			@ApiParam(name="organizationName",value="搜索相关机构名称",required=false) String organizationName,
			@ApiParam(name="warehouse_name",value="仓库名称",required=false) String warehouse_name,
			@ApiParam(name="key",value="搜索产品名称",required=false) String key) {
		ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(service.getTransferVoucherList(ctxUser.getSystemType(), ctxUser.getOrgId(), sn, organizationName, warehouse_name, voucherType,key));
	}
	
	@Log(title = "business.purchase.grn", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/transfer/record/detail", method = RequestMethod.GET)
	@ApiOperation(value = "被调单的单据明细列表", notes = "被调单的单据明细列表")
	public ResultList<TransferVoucherDetail> getTransferSaleRecordDetailList(
			@ApiParam(name="voucherType",value="调单类型",required=true) @RequestParam(required=true) RefVoucherType voucherType,
			@ApiParam(name="vId",value="调单单据内部编号(voucher_inner_sn)",required=true) @RequestParam(required=true)Long vId,
			@ApiParam(name="organization_inner_sn",value="相关机构内部编号(organization_inner_sn)",required=true) @RequestParam(required=true)Long organization_inner_sn,
			@ApiParam(name="billsType",value="单据类型",required=true) @RequestParam(required=true)BillsType billsType) {
		ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
		return ResultList.success(service.getTransferVoucherDetailList(ctxUser.getOrgId(), vId, organization_inner_sn, billsType, voucherType));
	}
}
