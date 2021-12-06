package com.yrt.project.api.business.contract.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.validation.annotation.Validated;

import com.yrt.common.dict.SystemType;
import com.yrt.project.api.business.contract.vo.AddContractApproveCommentForm;
import com.yrt.project.api.business.contract.vo.AddContractApproveForm;
import com.yrt.project.api.business.contract.vo.AddContractForm;
import com.yrt.project.api.business.contract.vo.AnalyseForm;
import com.yrt.project.api.business.contract.vo.ApproveContractForm;
import com.yrt.project.api.business.contract.vo.ContractApproveForm;
import com.yrt.project.api.business.contract.vo.ContractCIDForm;
import com.yrt.project.api.business.contract.vo.ContractForm;
import com.yrt.project.api.business.contract.vo.ContractSearchForm;
import com.yrt.project.api.business.contract.vo.EditContractForm;
import com.yrt.project.api.business.contract.vo.SearchContractDetailForm;
import com.yrt.project.api.business.contract.vo.SearchContractPriceForm;
import com.yrt.project.modular.contract.domain.Contract;
import com.yrt.project.modular.contract.domain.SearchContractDetail;

public interface IContractService {
	
	//编辑
	public Contract addContract(AddContractForm form, int status);
	
	public int editContract(EditContractForm form);
	
	public int deleteContract(ContractCIDForm ids);
	
	//查询
	public List<ContractForm> selectDraftContractList(ContractSearchForm form);
	
	public List<Contract> selectContractList(SystemType systemType, ContractSearchForm form);
	
	public Contract selectContractInfo(ContractCIDForm form);
	
	//审批
	public int addContractApprove(AddContractApproveForm form);
	
	public int addContractApproveComment(AddContractApproveCommentForm form);
	
	public List<ContractForm> selectContractApproveList(SystemType systemType, ContractSearchForm form);
	
	public List<ContractApproveForm> selectContractApproveCommentList(ContractCIDForm ids);
	
	public int agreeContractApprove(ApproveContractForm form);
	
	public int disagreeContractApprove(ApproveContractForm form);

	public int holdContractApprove(ApproveContractForm form);

	public int breakOffContract(ContractCIDForm form);

	public List<Map<String, Object>> analyseContract(@Validated AnalyseForm form);

	public List<BigDecimal> getContractPrice(@Validated SearchContractPriceForm form);

	public List<SearchContractDetail> searchContractDetailForm(SystemType type, @Validated SearchContractDetailForm form);
}
