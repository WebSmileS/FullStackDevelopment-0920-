package com.yrt.project.api.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.WarningType;
import com.yrt.common.utils.CompressedIdGenerator;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.project.api.common.vo.SearchWarningForm;
import com.yrt.project.modular.warning.domain.*;
import com.yrt.project.modular.warning.mapper.*;

@Service
public class WarningService {

	@Autowired WarningMapper mapper;
	
	@Autowired ProductMdrfWarningMapper mdrfWarningMapper;
	@Autowired ProductValidityWarningMapper validityWarningMapper;
	@Autowired CertWarningMapper certWarningMapper;
	
	//机构资证预警接口
	public List<CertWarning> getCertWarningList(SearchWarningForm form, ContextUserInfo curUser){
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", curUser.getOrgId());
		params.put("created_system_type", curUser.getSystemType());
		return certWarningMapper.selectCertWarningList(params);
	}
	
	@Transactional
	public CertWarning insertCertWarning(CertWarning form) {
		form.setId(CompressedIdGenerator.generateId());
		form.trim();
		
		certWarningMapper.insertCertWarning(form);
		return form;
	}
	
	//产品注册证预警接口
	public List<ProductMdrfWarning> getProductMdrfWarningList(SearchWarningForm form, ContextUserInfo curUser){
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", curUser.getOrgId());
		params.put("created_system_type", curUser.getSystemType());
		return mdrfWarningMapper.selectProductMdrfWarningList(params);
	}
	
	@Transactional
	public ProductMdrfWarning insertProductMdrfWarning(ProductMdrfWarning form) {
		form.setId(CompressedIdGenerator.generateId());
		
		mdrfWarningMapper.insertProductMdrfWarning(form);
		return form;
	}
	
	//产品效期预警接口
	public List<ProductValidityWarning> getProductValidityWarningList(SearchWarningForm form, ContextUserInfo curUser){
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", curUser.getOrgId());
		params.put("created_system_type", curUser.getSystemType());
		return validityWarningMapper.selectProductValidityWarningList(params);
	}
	
	@Transactional
	public ProductValidityWarning insertProductValidityWarning(ProductValidityWarning form) {
		form.setId(CompressedIdGenerator.generateId());
		
		validityWarningMapper.insertProductValidityWarning(form);
		return form;
	}
	
	@Transactional
	public void deleteWarning(Long orgId, SystemType systemType, WarningType warningType) {
		Map<String,Object> params = new HashMap<>();
		params.put("created_org_id", orgId);
		params.put("created_system_type", systemType);
		switch (warningType) {
		case CERT:
			certWarningMapper.trueDeleteCertWarning(params);
			break;
		case MDRF:
			mdrfWarningMapper.trueDeleteProductMdrfWarning(params);
			break;
		case VALIDITY:
			validityWarningMapper.trueDeleteProductValidityWarning(params);
			break;
		}
	}
	
	@Transactional
	public void cleanWarning() {
		Map<String,Object> params = new HashMap<>();
		certWarningMapper.trueDeleteCertWarning(params);
		mdrfWarningMapper.trueDeleteProductMdrfWarning(params);
		validityWarningMapper.trueDeleteProductValidityWarning(params);
	}
	
	public List<ProductValidityWarning> getExpireInventoryProductValidityList(Long orgId, SystemType systemType, Integer cew_day){
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", orgId);
		params.put("cew_day", cew_day);
		switch (systemType) {
		case HOSPITAL:
			return validityWarningMapper.checkHospitalExpireInventoryProductValidity(params);
		case DEALER:
			return validityWarningMapper.checkDealerExpireInventoryProductValidity(params);
		case VENDOR:
			return validityWarningMapper.checkVendorExpireInventoryProductValidity(params);
		default:
			break;
		}
		return new ArrayList<>();
	}
	
	public List<ProductMdrfWarning> getcheckExpireProductMdrfList(Long vid, Long pid, Integer cew_day){
		Map<String,Object> params = new HashMap<>();
		params.put("vendor_inner_sn", vid);
		params.put("product_inner_sn", pid);
		params.put("cew_day", cew_day);
		return mdrfWarningMapper.checkExpireProductMdrf(params);
	}
	
	public List<CertWarning> getCheckExpireCertList(Long orgId, SystemType systemType, Integer cew_day){
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", orgId);
		params.put("cew_day", cew_day);
		switch (systemType) {
		case HOSPITAL:
			return certWarningMapper.checkHospitalExpireCertWarning(params);
		case DEALER:
			return certWarningMapper.checkDealerrExpireCertWarning(params);
		case VENDOR:
			return certWarningMapper.checkVendorExpireCertWarning(params);
		default:
			break;
		}
		return new ArrayList<>();
	}
	
	/**
	 * 获取所有已认证的机构列表
	 * @return
	 */
	public List<AuthOrganization> getAllAuthOrg(){
		return mapper.selectAllAuthOrg();
	}
	
	/**
	 * 获取机构有效合同内的产品列表
	 * @param orgId
	 * @param systemType
	 * @return
	 */
	public List<ValidContractProduct> getValidContractProductList(Long orgId, SystemType systemType){
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", orgId);
		switch (systemType) {
		case HOSPITAL:
			return mapper.selectHospitalValidContractProduct(params);
		case DEALER:
			return mapper.selectDealerValidContractProduct(params);
		case VENDOR:
			return mapper.selectVendorValidContractProduct(params);
		default:
			break;
		}
		return new ArrayList<>();
	}
	
	public List<ValidContractOrg> getValidContractOrgList(Long orgId, SystemType systemType){
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", orgId);
		switch (systemType) {
		
		case HOSPITAL:
			return mapper.selectHospitalValidContractOrg(params);
		case DEALER:
			return mapper.selectDealerValidContractOrg(params);
		case VENDOR:
			return mapper.selectVendorValidContractOrg(params);
		default:
			break;
		}
		return new ArrayList<>();
	}
}
