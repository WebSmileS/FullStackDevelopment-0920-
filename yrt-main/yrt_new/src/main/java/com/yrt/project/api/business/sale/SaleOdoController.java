package com.yrt.project.api.business.sale;

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
import com.yrt.common.dict.SystemType;
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
import com.yrt.project.api.business.sale.vo.AddSaleOdoForm;
import com.yrt.project.api.business.sale.vo.EditSaleOdoForm;
import com.yrt.project.api.business.warehouse.vo.odo.OdoIDForm;
import com.yrt.project.api.business.warehouse.vo.odo.SearchOdoForm;
import com.yrt.project.modular.order.domain.SaleRecordDetail;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.warehouse.domain.Odo;
import com.yrt.project.modular.warehouse.domain.TransferSaleRecord;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/sale/odo")
@Api(tags = "业务-销售出库接口")
public class SaleOdoController extends BaseController {

	@Autowired SaleOdoService service;
	
	//TODO: 销售出库 == 原销售出库
	@SuppressWarnings("unchecked")
	@Log(title = "business.sale.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "搜索销售出库单列表(分页)", notes = "搜索销售出库单列表(分页)")
	public TableDataInfo<Odo> searchSaleOdoList(@Validated SearchOdoForm form) {
		List<Odo> res = null;
		//加入仓库限制
		List<Long> warehouseIds = getCurUserWarehouseIds();
		startPage();
		if (warehouseIds.size()==0) {
			res = new ArrayList<>();
		}else {
			res = service.searchSaleOdoList(form, warehouseIds);
		}
		return getDataTable(res);
	}
	
	@Log(title = "business.sale.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	@ApiOperation(value = "获取销售出库单详情", notes = "获取销售出库单详情")
	public ResultInfo<Odo> getSaleOdoInfo(@Validated OdoIDForm form) {
		return ResultInfo.success(service.selectSaleOdoInfo(form));
	}
	
	@Log(title = "business.sale.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/printed", method = RequestMethod.PUT)
	@ApiOperation(value = "销售出库单打印完成", notes = "销售出库单打印完成")
	public AjaxResult printed(
			@Validated OdoIDForm form) {
		service.printed(form);
		return success();
	}
	
	@Log(title = "business.sale.odo", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增销售出库单", notes = "新增销售出库单")
	public ResultInfo<Odo> insertSaleOdo(@RequestBody @Valid AddSaleOdoForm form) {
		OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
		return ResultInfo.success(service.insertSaleOdo(curUser, ServletUtils.getCurUserSystemType(), form));
	}

	@Log(title = "business.sale.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑销售出库单", notes = "编辑销售出库单")
	public ResultInfo<Odo> updateOdo(@RequestBody @Valid EditSaleOdoForm form) {
		OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
		return ResultInfo.success(service.updateSaleOdo(form, curUser));
	}

	@Log(title = "business.sale.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除销售出库单", notes = "删除销售出库单")
	public AjaxResult deleteOdo(@Validated OdoIDForm form) {
		return toAjax(service.deleteSaleOdo(form));
	}
	
	@Log(title = "business.sale.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/red", method = RequestMethod.PUT)
	@ApiOperation(value = "销售出库单红冲", notes = "销售出库单红冲")
	public ResultInfo<Odo> saleOdoRed(@RequestBody @Valid OdoIDForm form) {
		clearPage();
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.saleOdoRed(form, curUser));
	}
	
	@Log(title = "business.sale.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/unreview", method = RequestMethod.PUT)
	@ApiOperation(value = "销售出库单反审核", notes = "销售出库单反审核")
	public AjaxResult unReview(@RequestBody @Valid OdoIDForm form) {
		clearPage();
		service.saleOdoUnReview(form);
		return success();
	}
	
	@Log(title = "business.sale.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/sheet/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取销售出库单可调单类型列表", notes = "获取销售出库单可调单类型列表")
	public AjaxResult getOrderSheetList() {
		List<RefVoucherType> list = new ArrayList<>();
		SystemType systemType = ServletUtils.getCurUserSystemType();
		switch (systemType) {
		case HOSPITAL:
			return error("当前机构不支持该功能!");
		case DEALER:
			list.add(RefVoucherType.SALE_RECORD);
			break;
		case VENDOR:
			list.add(RefVoucherType.SALE_RECORD);
			break;
		default:
		}
		return success().put("list", list);
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.sale.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/transfer/record/list", method = RequestMethod.GET)
	@ApiOperation(value = "可用做销售出库调单的销售单列表", notes = "可用做销售出库调单的销售单列表")
	public TableDataInfo<TransferSaleRecord> getTransferSaleRecordList(
			@ApiParam(name="key",value="搜索关键字(销售单单号)",required=false) String key,
			@ApiParam(name="key1",value="搜索关键字(产品名字)",required=false) String key1,
			@ApiParam(name="organizationName",value="搜索相关机构名称",required=false) String organizationName) {
		ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(service.getTransferSaleRecordList(ctxUser.getSystemType(), ctxUser.getOrgId(), key,key1,organizationName));
	}
	
	@Log(title = "business.sale.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/transfer/record/detail", method = RequestMethod.GET)
	@ApiOperation(value = "被调单的销售单明细列表", notes = "被调单的销售单明细列表")
	public ResultList<SaleRecordDetail> getTransferSaleRecordDetailList(
			@ApiParam(name="sid",value="销售单内部编号(sl_inner_sn)",required=true) @RequestParam(required=true)Long sid,
			@ApiParam(name="billsType",value="单据类型",required=true) @RequestParam(required=true)BillsType billsType) {
		ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
		return ResultList.success(service.getTransferSaleRecordDetailList(ctxUser.getSystemType(), ctxUser.getOrgId(), sid, null, billsType));
	}
}
