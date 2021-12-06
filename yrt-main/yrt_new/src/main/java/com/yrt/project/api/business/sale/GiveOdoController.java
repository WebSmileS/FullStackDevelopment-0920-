package com.yrt.project.api.business.sale;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.yrt.framework.web.domain.ResultInfo;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.sale.vo.AddSaleOdoForm;
import com.yrt.project.api.business.sale.vo.EditSaleOdoForm;
import com.yrt.project.api.business.warehouse.vo.odo.OdoIDForm;
import com.yrt.project.api.business.warehouse.vo.odo.SearchOdoForm;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.warehouse.domain.Odo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/v1/business/give/odo")
@Api(tags = "业务-销售赠送单接口")
public class GiveOdoController extends BaseController {
	
	@Autowired GiveOdoService service;
	
	//TODO: 销售赠送 == 原零售价销售出库
	@SuppressWarnings("unchecked")
	@Log(title = "business.give.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	@ApiOperation(value = "搜索赠送单列表(分页)", notes = "搜索赠送单列表(分页)")
	public TableDataInfo<Odo> searchGiveOdoList(@Validated SearchOdoForm form) {
		List<Odo> res = null;
		//加入仓库限制
		List<Long> warehouseIds = getCurUserWarehouseIds();
		startPage();
		if (warehouseIds.size()==0) {
			res = new ArrayList<>();
		}else {
			res = service.searchGiveOdoList(form, warehouseIds);
		}
		return getDataTable(res);
	}
	
	@Log(title = "business.give.odo", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	@ApiOperation(value = "获取赠送单详情", notes = "获取赠送单详情")
	public ResultInfo<Odo> getGiveOdoInfo(@Validated OdoIDForm form) {
		return ResultInfo.success(service.selectGiveOdoInfo(form));
	}
	
	@Log(title = "business.give.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/printed", method = RequestMethod.PUT)
	@ApiOperation(value = "赠送单打印完成", notes = "赠送单打印完成")
	public AjaxResult printed(
			@Validated OdoIDForm form) {
		service.printed(form);
		return success();
	}
	
	@Log(title = "business.give.odo", businessType = BusinessType.INSERT, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "新增赠送单", notes = "新增赠送单")
	public ResultInfo<Odo> insertGiveOdo(@RequestBody @Valid AddSaleOdoForm form) {
		OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
		return ResultInfo.success(service.insertGiveOdo(curUser, ServletUtils.getCurUserSystemType(), form));
	}

	@Log(title = "business.give.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "编辑赠送单", notes = "编辑赠送单")
	public ResultInfo<Odo> updateOdo(@RequestBody @Valid EditSaleOdoForm form) {
		OrgEmployee curUser = ServletUtils.getCurUserInfo().getEmployee();
		return ResultInfo.success(service.updateGiveOdo(form, curUser));
	}

	@Log(title = "business.give.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除赠送单", notes = "删除赠送单")
	public AjaxResult deleteOdo(@Validated OdoIDForm form) {
		return toAjax(service.deleteGiveOdo(form));
	}
	
	@Log(title = "business.give.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/red", method = RequestMethod.PUT)
	@ApiOperation(value = "赠送单红冲", notes = "赠送单红冲")
	public ResultInfo<Odo> GiveOdoRed(@RequestBody @Valid OdoIDForm form) {
		clearPage();
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		return ResultInfo.success(service.GiveOdoRed(form, curUser));
	}
	
	@Log(title = "business.give.odo", businessType = BusinessType.UPDATE, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/unreview", method = RequestMethod.PUT)
	@ApiOperation(value = "赠送单反审核", notes = "赠送单反审核")
	public AjaxResult unReview(@RequestBody @Valid OdoIDForm form) {
		clearPage();
		service.GiveOdoUnReview(form);
		return success();
	}
}
