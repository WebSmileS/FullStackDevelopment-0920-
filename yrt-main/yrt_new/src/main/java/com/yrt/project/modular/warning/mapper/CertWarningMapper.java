package com.yrt.project.modular.warning.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.warning.domain.CertWarning;

@Repository
public interface CertWarningMapper {

	public void insertCertWarning(CertWarning params);
	
	public void trueDeleteCertWarning(Map<String,Object> params);
	
	public List<CertWarning> selectCertWarningList(Map<String,Object> params);
	
	/**
	 * 获取满足资证效期预警的机构列表
	 * @param params
	 * @return
	 */
	public List<CertWarning> checkHospitalExpireCertWarning(Map<String,Object> params);
	public List<CertWarning> checkDealerrExpireCertWarning(Map<String,Object> params);
	public List<CertWarning> checkVendorExpireCertWarning(Map<String,Object> params);
	
}
