package com.yrt.project.api.business.applyvoucher;

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
import com.yrt.project.modular.applyvoucher.domain.HospitalGrantVoucher;
import com.yrt.project.modular.applyvoucher.domain.HospitalGrantVoucherDetail;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.warehouse.domain.Odo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/grant/odo")
@Api(tags = "业务-领用出库接口")
public class GrantOdoController extends BaseController {
	@Autowired GrantOdoService service;
	
	//TODO: 领用出库 == 原领用出库
	@SuppressWarnings("unchecked")
	@Log(title = "business.grant.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "搜索领用出库单列表(分页)", notes = "搜索出库单列表(分页)")
	public TableDataInfo<Odo> searchGrantOdoList(@Validated SearchOdoForm form) {
		List<Odo> res = null;
		//加入仓库限制
		List<Long> warehouseIds = getCurUserWarehouseIds();
		startPage();
		if (warehouseIds.size()==0) {
			res = new ArrayList<>();
		}else {
			res = service.searchGrantOdoList(form, warehouseIds);
		}
		return getDataTable(res);
	}
	
	@Log(title = "business.grant.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	@ApiOperation(value = "获取领用出库单详情", notes = "获取领用出库单详情")
	public ResultInfo<Odo> getGrantOdoInfo(@Validated OdoIDForm form) {
		return ResultInfo.success(service.selectGrantOdoInfo(form));
	}
	
	@Log(title = "business.grant.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/printed", method = RequestMethod.PUT)
	@ApiOperation(value = "领用出库单打印完成", notes = "领用出库单打印完成")
	public AjaxResult printed(
			@Validated OdoIDForm form) {
		service.printed(form);
		return success();
	}
	
	@Log(title = "business.grant.odo", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增领用出库单", notes = "新增领用出库单")
	public ResultInfo<Odo> insertGrantOdo(@RequestBody @Valid AddSaleOdoForm form) {
		OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
		return ResultInfo.success(service.insertGrantOdo(curUser, ServletUtils.getCurUserSystemType(), form));
	}

	@Log(title = "business.grant.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑领用出库单", notes = "编辑领用出库单")
	public ResultInfo<Odo> updateOdo(@RequestBody @Valid EditSaleOdoForm form) {
		OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
		return ResultInfo.success(service.updateGrantOdo(form, curUser));
	}

	@Log(title = "business.grant.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除领用出库单", notes = "删除领用出库单")
	public AjaxResult deleteOdo(@Validated OdoIDForm form) {
		return toAjax(service.deleteGrantOdo(form));
	}
	
	@Log(title = "business.grant.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/red", method = RequestMethod.PUT)
	@ApiOperation(value = "领用出库单红冲", notes = "领用出库单红冲")
	public ResultInfo<Odo> grantOdoRed(@RequestBody @Valid OdoIDForm form) {
		clearPage();
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.grantOdoRed(form, curUser));
	}
	
	@Log(title = "business.grant.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/unreview", method = RequestMethod.PUT)
	@ApiOperation(value = "领用出库单反审核", notes = "领用出库单反审核")
	public AjaxResult unReview(@RequestBody @Valid OdoIDForm form) {
		clearPage();
		service.grantOdoUnReview(form);
		return success();
	}
	
	@Log(title = "business.grant.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/sheet/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取领用出库单可调单类型列表", notes = "获取领用出库单可调单类型列表")
	public AjaxResult getOrderSheetList() {
		List<RefVoucherType> list = new ArrayList<>();
		SystemType systemType = ServletUtils.getCurUserSystemType();
		switch (systemType) {
		case HOSPITAL:
			list.add(RefVoucherType.GRANT);
			break;
		case DEALER:
		case VENDOR:
			return error("当前机构不支持该功能!");
		default:
		}
		return success().put("list", list);
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.grant.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/transfer/grant/list", method = RequestMethod.GET)
	@ApiOperation(value = "可用做领用出库调单的发放单列表", notes = "可用做领用出库调单的发放单列表")
	public TableDataInfo<HospitalGrantVoucher> getTransferGrantList(
			@ApiParam(name="key",value="搜索关键字(发放单单号)",required=false) String key,
			@ApiParam(name="key1",value="搜索关键字(产品名字)",required=false) String key1) {
		ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
		startPage();
		return getDataTable(service.getTransferGrantList(ctxUser.getSystemType(), ctxUser.getOrgId(), key,key1));
	}
	
	@Log(title = "business.grant.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/transfer/grant/detail", method = RequestMethod.GET)
	@ApiOperation(value = "被调单的发放单发放单明细列表", notes = "被调单的发放单明细列表")
	public ResultList<HospitalGrantVoucherDetail> getTransferGrantDetailList(
			@ApiParam(name="gvid",value="发放单内部编号(gv_inner_sn)",required=true) @RequestParam(required=true)Long gvid) {
		ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
		return ResultList.success(service.getTransferGrantDetailList(ctxUser.getSystemType(), ctxUser.getOrgId(), gvid, null));
	}
}
