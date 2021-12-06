package com.yrt.project.api.business.certificate.service;

import java.util.List;
import java.util.Map;

import com.yrt.common.dict.SystemType;
import com.yrt.project.api.business.certificate.vo.CertSearchOrgForm;

public interface ICertificateService {

	public List<Map<String,Object>> selectContractVendor(SystemType systemType, CertSearchOrgForm form);
	
	public List<Map<String,Object>> selectContractHospital(SystemType systemType, CertSearchOrgForm form);
	
	public List<Map<String,Object>> selectContractDealer(SystemType systemType, CertSearchOrgForm form);

	public int updateCertstatus(SystemType systemType,Long oid);
	
}
