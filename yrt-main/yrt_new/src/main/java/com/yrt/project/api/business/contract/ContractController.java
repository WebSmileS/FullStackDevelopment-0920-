package com.yrt.project.api.business.contract;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.DBException;
import com.yrt.common.utils.ServletUtils;
import com.yrt.framework.aspectj.lang.annotation.Log;
import com.yrt.framework.aspectj.lang.enums.BusinessType;
import com.yrt.framework.aspectj.lang.enums.OperatorType;
import com.yrt.framework.web.controller.BaseController;
import com.yrt.framework.web.domain.*;
import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.business.contract.service.IContractService;
import com.yrt.project.api.business.contract.vo.AddContractApproveCommentForm;
import com.yrt.project.api.business.contract.vo.AddContractApproveForm;
import com.yrt.project.api.business.contract.vo.AddContractForm;
import com.yrt.project.api.business.contract.vo.AnalyseForm;
import com.yrt.project.api.business.contract.vo.ApproveContractForm;
import com.yrt.project.api.business.contract.vo.ContractCIDForm;
import com.yrt.project.api.business.contract.vo.ContractForm;
import com.yrt.project.api.business.contract.vo.ContractSearchForm;
import com.yrt.project.api.business.contract.vo.EditContractForm;
import com.yrt.project.api.business.contract.vo.SearchContractDetailForm;
import com.yrt.project.api.business.contract.vo.SearchContractPriceForm;
import com.yrt.project.modular.contract.domain.Contract;
import com.yrt.project.modular.contract.domain.SearchContractDetail;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/v1/business/contract")
@Api(tags = "??????-??????????????????")
public class ContractController extends BaseController {

	@Autowired
	private IContractService service;

	// ??????
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "????????????", notes = "????????????")
	public ResultInfo<Contract> addContract(@RequestBody @Valid AddContractForm form) {
		return ResultInfo.success(service.addContract(form, 0));
	}
	
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "??????????????????", notes = "??????????????????")
	public AjaxResult updateContract(@RequestBody @Valid EditContractForm form) {
		return toAjax(service.editContract(form));
	}

	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "??????????????????", notes = "??????????????????")
	public AjaxResult deleteContract(@Validated ContractCIDForm ids) {
		return toAjax(service.deleteContract(ids));
	}

	// ??????
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	@ApiOperation(value = "??????????????????", notes = "??????????????????")
	public ResultInfo<Contract> selectContractInfo(@Validated ContractCIDForm form) {
		return ResultInfo.success(service.selectContractInfo(form));
	}

	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/draf/list", method = RequestMethod.GET)
	@ApiOperation(value = "????????????????????????", notes = "????????????????????????")
	public AjaxResult selectDrafContractList(@Validated ContractSearchForm form) {
//		startPage();
		List<ContractForm> list = service.selectDraftContractList(form);
		return success().put("rows", list);
	}

	@SuppressWarnings("unchecked")
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list/{systemType}", method = RequestMethod.GET)
	@ApiOperation(value = "??????????????????????????????(??????)", notes = "??????????????????????????????(??????)")
	public TableDataInfo<Contract> selectContractList(
			@ApiParam(name="systemType",value="?????????????????????(????????????tab???):1-?????? ??? 2-???????????? 3-??????",required=true) @PathVariable int systemType,
			@Validated ContractSearchForm form) {
		startPage();
		List<Contract> list = service.selectContractList(convertSystemType(systemType), form);
		return getDataTable(list);
	}

	// ??????
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/approve/add", method = RequestMethod.POST)
	@ApiOperation(value = "????????????????????????", notes = "????????????????????????")
	public AjaxResult addContractApprove(@RequestBody @Valid AddContractApproveForm form) {
		return toAjax(service.addContractApprove(form));
	}

	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/approve/list", method = RequestMethod.GET)
	@ApiOperation(value = "????????????????????????", notes = "????????????????????????")
	public AjaxResult selectApproveList(
			@Validated ContractSearchForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		return success().put("list", service.selectContractApproveList(systemType, form));
	}

	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/approve/comment/list", method = RequestMethod.GET)
	@ApiOperation(value = "??????????????????????????????", notes = "??????????????????????????????")
	public AjaxResult selectApproveCommentList(@Validated ContractCIDForm form) {
		return success().put("list", service.selectContractApproveCommentList(form));
	}

	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/approve/comment/add", method = RequestMethod.POST)
	@ApiOperation(value = "????????????????????????", notes = "????????????????????????")
	public AjaxResult addContractApproveComment(@Validated AddContractApproveCommentForm form) {
		return toAjax(service.addContractApproveComment(form));
	}

	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/approve/approve", method = RequestMethod.PUT)
	@ApiOperation(value = "????????????", notes = "????????????")
	public AjaxResult approveContract(@Validated ApproveContractForm form) {
		switch (form.getStatus()) {
		case 1:
			return toAjax(service.agreeContractApprove(form));
		case 2:
			return toAjax(service.disagreeContractApprove(form));
		case 3:
			return toAjax(service.holdContractApprove(form));
		default:
			return null;
		}
	}
	
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/stop", method = RequestMethod.PUT)
	@ApiOperation(value = "????????????", notes = "????????????")
	public AjaxResult breakoff(@Validated ContractCIDForm form) {
		return toAjax(service.breakOffContract(form));
	}
	
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/makeup", method = RequestMethod.POST)
	@ApiOperation(value = "????????????", notes = "????????????")
	public ResultInfo<Contract> makeupContract(@RequestBody @Valid AddContractForm form) {
		return ResultInfo.success(service.addContract(form, 4));
	}
	
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/change", method = RequestMethod.PUT)
	@ApiOperation(value = "??????????????????", notes = "??????????????????")
	public AjaxResult changeContract() {
		throw new DBException("?????????????????????!");//TODO: ?????????
	}
	
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/analyse", method = RequestMethod.GET)
	@ApiOperation(value = "??????????????????", notes = "??????????????????")
	public AjaxResult analyseContract(
			@RequestBody @Valid AnalyseForm form) {
		if(!form.canAnalyse()) {
			throw new DBException("?????????????????????????????????!");
		}
		return success().put("list", service.analyseContract(form));
	}
	
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/getContractPrice", method = RequestMethod.POST)
	@ApiOperation(value = "????????????????????????", notes = "????????????????????????")
	public AjaxResult getContractPrice(
			@RequestBody @Valid SearchContractPriceForm form) {
		return success().put("list", service.getContractPrice(form));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/searchContractDetail", method = RequestMethod.POST)
	@ApiOperation(value = "??????????????????(??????)", notes = "??????????????????(??????)")
	public TableDataInfo<SearchContractDetail> searchContractDetails(
			@RequestBody @Valid SearchContractDetailForm form) {
		startPage();
		return getDataTable(service.searchContractDetailForm(ServletUtils.getCurUserSystemType(), form));
	}
}
