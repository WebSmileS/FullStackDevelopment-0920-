package com.yrt.project.api.platform.service;

import com.yrt.common.dict.SystemType;
import com.yrt.project.api.platform.vo.organization.AddHospitalPLHMIForm;
import com.yrt.project.api.platform.vo.organization.AddOrgLicenseForm;
import com.yrt.project.api.platform.vo.organization.EditOrgInfo;
import com.yrt.project.api.platform.vo.organization.HospitalPLHMIForm;
import com.yrt.project.api.platform.vo.organization.OrgLicenseForm;
import com.yrt.project.modular.organization.domain.Company;

public interface IHospitalOrgService extends IOrgService{

	public static int CERT_TYPE_OTHER = 0;
	public static int CERT_TYPE_LICENSE = 1;
	public static int CERT_TYPE_PLHMI = 2;
	public static int CERT_TYPE_KH = 3;
			
	public Company selectNoAuditOrgInfo(Long id);
	
	public int updateNoAuditOrgInfo(Long id, EditOrgInfo org, SystemType curUserSystemType);
	
	public int deleteNoAuditOrgInfo(Long id);
	
	public OrgLicenseForm selectOrgLicenseInfoById(Long id);
	
	public int insertOrgLicenseInfo(Long id, AddOrgLicenseForm form);

	public int updateOrgLicenseInfo(Long id, OrgLicenseForm form, SystemType curUserSystemType);
	
	//医院医疗机构执业许可证
	public HospitalPLHMIForm selectHospitalPLHMI(Long id);
	
	public int insertHospitalPLHMI(Long id, AddHospitalPLHMIForm form);
	
	public int updateHospitalPLHMI(Long id, HospitalPLHMIForm form, SystemType curUserSystemType);

}
