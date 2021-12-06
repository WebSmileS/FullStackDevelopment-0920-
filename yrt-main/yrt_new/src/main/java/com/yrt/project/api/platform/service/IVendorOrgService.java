package com.yrt.project.api.platform.service;

import com.yrt.common.dict.SystemType;
import com.yrt.project.api.platform.vo.organization.AddOrgLicenseForm;
import com.yrt.project.api.platform.vo.organization.AddVendorForm;
import com.yrt.project.api.platform.vo.organization.AddVendorMDPLForm;
import com.yrt.project.api.platform.vo.organization.EditOrgInfo;
import com.yrt.project.api.platform.vo.organization.OrgLicenseForm;
import com.yrt.project.api.platform.vo.organization.VendorMDPLForm;
import com.yrt.project.modular.organization.domain.Company;
import com.yrt.project.modular.organization.domain.Vendor;

public interface IVendorOrgService extends IOrgService {
	
	public static int CERT_TYPE_OTHER = 0;
	public static int CERT_TYPE_LICENSE = 1;
	public static int CERT_TYPE_MDPL = 2;
	public static int CERT_TYPE_ANNUAL_PUBLICITY = 3;
	public static int CERT_TYPE_SEAL = 4;
	public static int CERT_TYPE_OUT_ORDER = 5;
	public static int CERT_TYPE_QMS = 6;
	public static int CERT_TYPE_CONTRACT = 7;
	
	public Company selectNoAuditOrgInfo(Long id);
	
	public int updateNoAuditOrgInfo(Long id, EditOrgInfo org, SystemType curUserSystemType);
	
	public int deleteNoAuditOrgInfo(Long id);
	
	public OrgLicenseForm selectOrgLicenseInfoById(Long id);
	
	public int insertOrgLicenseInfo(Long id, AddOrgLicenseForm form);

	public int updateOrgLicenseInfo(Long id, OrgLicenseForm form, SystemType curUserSystemType);
	
	
	//厂商医疗器械生产许可证
	
	public VendorMDPLForm selectVendorMDPLById(Long id);
	
	public int insertVendorMDPL(long id, AddVendorMDPLForm mdpl);
	
	public int updateVendorMDPL(long id, VendorMDPLForm mdpl, SystemType curUserSystemType);
	
	//新增厂商
	public Vendor insertVendor(AddVendorForm form);
	
}
