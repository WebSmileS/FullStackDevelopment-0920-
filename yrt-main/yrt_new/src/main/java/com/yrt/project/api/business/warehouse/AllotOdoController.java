package com.yrt.project.api.business.warehouse;

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
import com.yrt.project.api.business.warehouse.service.AllotOdoService;
import com.yrt.project.api.business.warehouse.vo.odo.OdoIDForm;
import com.yrt.project.api.business.warehouse.vo.odo.SearchOdoForm;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.warehouse.domain.AllotVoucher;
import com.yrt.project.modular.warehouse.domain.AllotVoucherDetail;
import com.yrt.project.modular.warehouse.domain.Odo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/allot/odo")
@Api(tags = "业务-调拨出库接口")
public class AllotOdoController extends BaseController {

	@Autowired AllotOdoService service;
	
	//TODO: 调拨出库 == 原调拨出库
	@SuppressWarnings("unchecked")
	@Log(title = "business.allot.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "搜索调拨出库单列表(分页)", notes = "搜索调拨出库单列表(分页)")
	public TableDataInfo<Odo> searchAllotOdoList(@Validated SearchOdoForm form) {
		List<Odo> res = null;
		//加入仓库限制
		List<Long> warehouseIds = getCurUserWarehouseIds();
		startPage();
		if (warehouseIds.size()==0) {
			res = new ArrayList<>();
		}else {
			res = service.searchAllotOdoList(form, warehouseIds);
		}
		return getDataTable(res);
	}
	
	@Log(title = "business.allot.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	@ApiOperation(value = "获取调拨出库单详情", notes = "获取调拨出库单详情")
	public ResultInfo<Odo> getAllotOdoInfo(@Validated OdoIDForm form) {
		return ResultInfo.success(service.selectAllotOdoInfo(form));
	}
	
	@Log(title = "business.allot.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/printed", method = RequestMethod.PUT)
	@ApiOperation(value = "调拨出库单打印完成", notes = "调拨出库单打印完成")
	public AjaxResult printed(
			@Validated OdoIDForm form) {
		service.printed(form);
		return success();
	}
	
	@Log(title = "business.allot.odo", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增调拨出库单", notes = "新增调拨出库单")
	public ResultInfo<Odo> insertAllotOdo(@RequestBody @Valid AddSaleOdoForm form) {
		OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
		return ResultInfo.success(service.insertAllotOdo(curUser, ServletUtils.getCurUserSystemType(), form));
	}

	@Log(title = "business.allot.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑调拨出库单", notes = "编辑调拨出库单")
	public ResultInfo<Odo> updateAllotOdo(@RequestBody @Valid EditSaleOdoForm form) {
		OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
		return ResultInfo.success(service.updateAllotOdo(form, curUser));
	}

	@Log(title = "business.allot.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除调拨出库单", notes = "删除调拨出库单")
	public AjaxResult deleteOdo(@Validated OdoIDForm form) {
		return toAjax(service.deleteAllotOdo(form));
	}
	
	@Log(title = "business.allot.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/red", method = RequestMethod.PUT)
	@ApiOperation(value = "调拨出库单红冲", notes = "调拨出库单红冲")
	public ResultInfo<Odo> AllotOdoRed(@RequestBody @Valid OdoIDForm form) {
		clearPage();
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.allotOdoRed(form, curUser));
	}
	
	@Log(title = "business.allot.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/unreview", method = RequestMethod.PUT)
	@ApiOperation(value = "调拨出库单反审核", notes = "调拨出库单反审核")
	public AjaxResult unReview(@RequestBody @Valid OdoIDForm form) {
		clearPage();
		service.allotOdoUnReview(form);
		return success();
	}
	
	@Log(title = "business.allot.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/sheet/list", method = RequestMethod.GET)
	@ApiOperation(value = "获取调拨出库可调单类型列表", notes = "获取调拨出库可调单类型列表")
	public AjaxResult getOrderSheetList() {
		List<RefVoucherType> list = new ArrayList<>();
		SystemType systemType = ServletUtils.getCurUserSystemType();
		switch (systemType) {
		case HOSPITAL:
			list.add(RefVoucherType.ALLOT);
			break;
		case DEALER:
			list.add(RefVoucherType.ALLOT);
			break;
		case VENDOR:
			return error("当前机构不支持该功能!");
		default:
		}
		return success().put("list", list);
	}
	
	@SuppressWarnings("unchecked")
    @Log(title = "business.allot.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/transfer/voucher", method = RequestMethod.GET)
    @ApiOperation(value = "可用做调拨出库调单的调拨单列表", notes = "可用做调拨出库调单的调拨单列表")
    public TableDataInfo<AllotVoucher> getTransferAllotVoucherList(
            @ApiParam(name="key",value="搜索关键字(调拨单外部单据编号)") String key,
            @ApiParam(name="key1",value="搜索关键字(产品名字)") String key1) {
        ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
        List<AllotVoucher> res = null;
		//加入仓库限制
		List<Long> warehouseIds = getCurUserWarehouseIds();
		startPage();
		if (warehouseIds.size()==0) {
			res = new ArrayList<>();
		}else {
			res = service.getTransferAllotVoucherList(ctxUser.getOrgId(), key,key1,warehouseIds);
		}
		return getDataTable(res);
    }

    @Log(title = "business.allot.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
    @RequestMapping(value = "/transfer/detail", method = RequestMethod.GET)
    @ApiOperation(value = "被调单的调拨单明细列表", notes = "被调单的调拨单明细列表，调单时的查询接口")
    public ResultList<AllotVoucherDetail> getTransferAllotVoucherDetailList(
            @ApiParam(name="avid",value="调拨单内部编号(av_inner_sn)",required=true) @RequestParam Long avid) {
        ContextUserInfo ctxUser = ServletUtils.getCurUserInfo();
        return ResultList.success(service.getTransferAllotVoucherDetailList(ctxUser.getOrgId(), avid));
    }
}
