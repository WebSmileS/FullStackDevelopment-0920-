package com.yrt.project.api.business.certificate.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yrt.common.dict.SystemType;
import com.yrt.project.api.business.certificate.service.ICertificateService;
import com.yrt.project.api.business.certificate.vo.CertSearchOrgForm;
import com.yrt.project.modular.organization.mapper.DealerMapper;
import com.yrt.project.modular.organization.mapper.HospitalMapper;
import com.yrt.project.modular.organization.mapper.VendorMapper;

@Service
public class CertificateServiceImpl implements ICertificateService {

	@Autowired
	private HospitalMapper hMapper;
	@Autowired
	private VendorMapper vMapper;
	@Autowired
	private DealerMapper dMapper;
	
	@Override
	public List<Map<String, Object>> selectContractVendor(SystemType systemType, CertSearchOrgForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", form.getOid());
		params.put("name", form.getName());
		params.put("social_credit_code", form.getSocial_credit_code());
		params.put("administrative_division_sn", form.getAdministrative_division_sn());
		switch (systemType) {
		case HOSPITAL:
			return hMapper.selectContractVendor(params);
		case DEALER:
			return dMapper.selectContractVendor(params);
		default:
		}
		return new ArrayList<Map<String, Object>>();
	}

	@Override
	public List<Map<String, Object>> selectContractHospital(SystemType systemType, CertSearchOrgForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", form.getOid());
		params.put("name", form.getName());
		params.put("social_credit_code", form.getSocial_credit_code());
		params.put("administrative_division_sn", form.getAdministrative_division_sn());
		switch (systemType) {
		case VENDOR:
			return vMapper.selectContractHospital(params);
		case DEALER:
			return dMapper.selectContractHospital(params);
		default:
		}
		return new ArrayList<Map<String, Object>>();
	}

	@Override
	public List<Map<String, Object>> selectContractDealer(SystemType systemType, CertSearchOrgForm form) {
		Map<String, Object> params = new HashMap<>();
		params.put("id", form.getOid());
		params.put("name", form.getName());
		params.put("social_credit_code", form.getSocial_credit_code());
		params.put("administrative_division_sn", form.getAdministrative_division_sn());
		switch (systemType) {
		case HOSPITAL:
			return hMapper.selectContractDealer(params);
		case DEALER:
			return dMapper.selectContractDealer(params);
		case VENDOR:
			return vMapper.selectContractDealer(params);
		default:
		}
		return new ArrayList<Map<String, Object>>();
	}

	@Override
	public int updateCertstatus (SystemType systemType,Long oid) {
		switch (systemType) {
		case HOSPITAL:
			return hMapper.updateCertifieStatus1(oid);
		case DEALER:
			return dMapper.updateCertifieStatus1(oid);
		case VENDOR:
			return vMapper.updateCertifieStatus1(oid);
		default:
			break;
		}
		return 1;
	}
	
}
