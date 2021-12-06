package com.yrt.project.modular.warning.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.warning.domain.AuthOrganization;
import com.yrt.project.modular.warning.domain.ValidContractOrg;
import com.yrt.project.modular.warning.domain.ValidContractProduct;

@Repository
public interface WarningMapper {
	
	public List<AuthOrganization> selectAllAuthOrg();
	
	public List<ValidContractProduct> selectHospitalValidContractProduct(Map<String,Object> params);
	public List<ValidContractProduct> selectDealerValidContractProduct(Map<String,Object> params);
	public List<ValidContractProduct> selectVendorValidContractProduct(Map<String,Object> params);
	
	public List<ValidContractOrg> selectHospitalValidContractOrg(Map<String,Object> params);
	public List<ValidContractOrg> selectDealerValidContractOrg(Map<String,Object> params);
	public List<ValidContractOrg> selectVendorValidContractOrg(Map<String,Object> params);
}
