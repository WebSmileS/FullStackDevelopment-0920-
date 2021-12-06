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
@Api(tags = "业务-合同管理接口")
public class ContractController extends BaseController {

	@Autowired
	private IContractService service;

	// 编辑
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ApiOperation(value = "添加合同", notes = "添加合同")
	public ResultInfo<Contract> addContract(@RequestBody @Valid AddContractForm form) {
		return ResultInfo.success(service.addContract(form, 0));
	}
	
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	@ApiOperation(value = "修改合同信息", notes = "修改合同信息")
	public AjaxResult updateContract(@RequestBody @Valid EditContractForm form) {
		return toAjax(service.editContract(form));
	}

	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/del", method = RequestMethod.DELETE)
	@ApiOperation(value = "删除合同信息", notes = "删除合同信息")
	public AjaxResult deleteContract(@Validated ContractCIDForm ids) {
		return toAjax(service.deleteContract(ids));
	}

	// 查询
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	@ApiOperation(value = "查看合同信息", notes = "查看合同信息")
	public ResultInfo<Contract> selectContractInfo(@Validated ContractCIDForm form) {
		return ResultInfo.success(service.selectContractInfo(form));
	}

	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/draf/list", method = RequestMethod.GET)
	@ApiOperation(value = "查看草稿合同列表", notes = "查看草稿合同列表")
	public AjaxResult selectDrafContractList(@Validated ContractSearchForm form) {
//		startPage();
		List<ContractForm> list = service.selectDraftContractList(form);
		return success().put("rows", list);
	}

	@SuppressWarnings("unchecked")
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/list/{systemType}", method = RequestMethod.GET)
	@ApiOperation(value = "根据条件查看合同列表(分页)", notes = "根据条件查看合同列表(分页)")
	public TableDataInfo<Contract> selectContractList(
			@ApiParam(name="systemType",value="合同合作方类型(对应前端tab页):1-医院 、 2-经销商、 3-厂商",required=true) @PathVariable int systemType,
			@Validated ContractSearchForm form) {
		startPage();
		List<Contract> list = service.selectContractList(convertSystemType(systemType), form);
		return getDataTable(list);
	}

	// 审批
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/approve/add", method = RequestMethod.POST)
	@ApiOperation(value = "添加合同审批流程", notes = "添加合同审批流程")
	public AjaxResult addContractApprove(@RequestBody @Valid AddContractApproveForm form) {
		return toAjax(service.addContractApprove(form));
	}

	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/approve/list", method = RequestMethod.GET)
	@ApiOperation(value = "查看审批合同列表", notes = "查看审批合同列表")
	public AjaxResult selectApproveList(
			@Validated ContractSearchForm form) {
		SystemType systemType = ServletUtils.getCurUserSystemType();
		return success().put("list", service.selectContractApproveList(systemType, form));
	}

	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/approve/comment/list", method = RequestMethod.GET)
	@ApiOperation(value = "查看合同审批备注列表", notes = "查看合同审批备注列表")
	public AjaxResult selectApproveCommentList(@Validated ContractCIDForm form) {
		return success().put("list", service.selectContractApproveCommentList(form));
	}

	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/approve/comment/add", method = RequestMethod.POST)
	@ApiOperation(value = "添加合同审批备注", notes = "添加合同审批备注")
	public AjaxResult addContractApproveComment(@Validated AddContractApproveCommentForm form) {
		return toAjax(service.addContractApproveComment(form));
	}

	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/approve/approve", method = RequestMethod.PUT)
	@ApiOperation(value = "审批合同", notes = "审批合同")
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
	@ApiOperation(value = "中止合同", notes = "中止合同")
	public AjaxResult breakoff(@Validated ContractCIDForm form) {
		return toAjax(service.breakOffContract(form));
	}
	
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/makeup", method = RequestMethod.POST)
	@ApiOperation(value = "补录合同", notes = "补录合同")
	public ResultInfo<Contract> makeupContract(@RequestBody @Valid AddContractForm form) {
		return ResultInfo.success(service.addContract(form, 4));
	}
	
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/change", method = RequestMethod.PUT)
	@ApiOperation(value = "变更合同信息", notes = "变更合同信息")
	public AjaxResult changeContract() {
		throw new DBException("该功能还未开发!");//TODO: 未开发
	}
	
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/analyse", method = RequestMethod.GET)
	@ApiOperation(value = "销售冲突分析", notes = "销售冲突分析")
	public AjaxResult analyseContract(
			@RequestBody @Valid AnalyseForm form) {
		if(!form.canAnalyse()) {
			throw new DBException("非医院合同不支持该功能!");
		}
		return success().put("list", service.analyseContract(form));
	}
	
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/getContractPrice", method = RequestMethod.POST)
	@ApiOperation(value = "查询产品合同价格", notes = "查询产品合同价格")
	public AjaxResult getContractPrice(
			@RequestBody @Valid SearchContractPriceForm form) {
		return success().put("list", service.getContractPrice(form));
	}
	
	@SuppressWarnings("unchecked")
	@Log(title = "business.contract", businessType = BusinessType.OTHER, operatorType = OperatorType.BUSINESS)
	@RequestMapping(value = "/searchContractDetail", method = RequestMethod.POST)
	@ApiOperation(value = "查询合同产品(分页)", notes = "查询合同产品(分页)")
	public TableDataInfo<SearchContractDetail> searchContractDetails(
			@RequestBody @Valid SearchContractDetailForm form) {
		startPage();
		return getDataTable(service.searchContractDetailForm(ServletUtils.getCurUserSystemType(), form));
	}
}
