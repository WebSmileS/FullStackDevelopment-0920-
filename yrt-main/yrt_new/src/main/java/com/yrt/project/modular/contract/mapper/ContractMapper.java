package com.yrt.project.modular.contract.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.api.business.contract.vo.ContractSearchForm;
import com.yrt.project.modular.contract.domain.Contract;
import com.yrt.project.modular.contract.domain.ContractAttachment;
import com.yrt.project.modular.contract.domain.ContractProduct;
import com.yrt.project.modular.contract.domain.ContractVendorProduct;
import com.yrt.project.modular.contract.domain.SearchContractDetail;

@Repository
public interface ContractMapper {

	/**
	 * 合同部分
	 */
	public Contract selectContractInfo(Map<String, Object> params);
	public void addContract(Map<String, Object> params);
	public int updateContract(Map<String, Object> params);
	public int deleteContract(Map<String, Object> params);
	public List<Contract> selectContractList(ContractSearchForm form);
	public List<ContractAttachment> selectContractAttachment(Map<String, Object> params);
	public void  addContractAttachment(Map<String, Object> params);
	public int deleteContractAttachment(Map<String, Object> params);
	public int deleteAllContractAttachment(Map<String, Object> params);
	public List<Contract> selectAllContract();
	public int startContract (Map<String, Object> params);
	public int stopContract(Map<String, Object> params);
	
	/**
	 * 商品部分
	 */
	public int insertContractProduct(Map<String, Object> params);
	public int insertContractDealerProduct(Map<String, Object> params);
	public int updateContractProduct(Map<String, Object> params);
	public int updateContractDealerProduct(Map<String, Object> params);
	public int deleteContractProduct(Map<String, Object> params);
	public int deleteContractDealerProduct(Map<String, Object> params);
	public int deleteAllContractProduct(Map<String, Object> params);
	public int deleteAllContractDealerProduct(Map<String, Object> params);
	public List<ContractVendorProduct> selectAllContractProductList(Map<String, Object> params);
	public List<ContractProduct> selectContractProductList(Map<String, Object> params);
	public List<ContractProduct> selectContractDealerProductList(Map<String, Object> params);
	public List<SearchContractDetail> searchContractDetail(Map<String, Object> params);
	
	/**
	 * 审批部分
	 */
	public List<Map<String,Object>> selectContractApproveCommentList(Map<String, Object> params);
	public List<Map<String,Object>> selectContracctListByApprover(Map<String, Object> params);
	public void addContractApprove(Map<String, Object> params);
	public int  updateContractApprove(Map<String, Object> params);
	public String selectContractApproveNextId(Map<String, Object> params);
	public void addContractApproveComment(Map<String, Object> params);
	
	/**
	 * 医院特有部分
	 * @param params
	 */
	public void insertCRelHD(Map<String, Object> params);
	public void deleteCRelHD(Map<String, Object> params);
	public List<Map<String,Object>> analyseContract(Map<String, Object> params);
}
