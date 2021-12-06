package com.yrt.project.api.platform.service;

import com.yrt.common.dict.SystemType;
import com.yrt.project.api.platform.vo.organization.AddDealerMAAIOLForm;
import com.yrt.project.api.platform.vo.organization.AddDealerSTMDBRCForm;
import com.yrt.project.api.platform.vo.organization.AddOrgLicenseForm;
import com.yrt.project.api.platform.vo.organization.DealerMAAIOLForm;
import com.yrt.project.api.platform.vo.organization.DealerSTMDBRCForm;
import com.yrt.project.api.platform.vo.organization.EditOrgInfo;
import com.yrt.project.api.platform.vo.organization.OrgLicenseForm;
import com.yrt.project.modular.organization.domain.Company;

public interface IDealerOrgService extends IOrgService {

	public static int CERT_TYPE_OTHER = 0;
	public static int CERT_TYPE_LICENSE = 1;
	public static int CERT_TYPE_MAAIOL = 2;
	public static int CERT_TYPE_STMDBRC = 3;
	public static int CERT_TYPE_ANNUAL_PUBLICITY = 4;
	public static int CERT_TYPE_SEAL = 5;
	public static int CERT_TYPE_OUT_ORDER = 6;
	public static int CERT_TYPE_QMS = 7;
	public static int CERT_TYPE_CONTRACT = 8;
	
	public Company selectNoAuditOrgInfo(Long id);
	
	public int updateNoAuditOrgInfo(Long id, EditOrgInfo org, SystemType curUserSystemType);
	
	public int deleteNoAuditOrgInfo(Long id);
	//经销商营业执照
	public OrgLicenseForm selectOrgLicenseInfoById(Long id);
	
	public int insertOrgLicenseInfo(Long id, AddOrgLicenseForm form);

	public int updateOrgLicenseInfo(Long id, OrgLicenseForm form, SystemType curUserSystemType);
	//经销商医疗器械经营许可证(就是第三类)
	public DealerMAAIOLForm selectDealerMAAIOL(Long id);
	
	public int insertDealerMAAIOL(Long id, AddDealerMAAIOLForm form);
	
	public int updateDealerMAAIOL(Long id, DealerMAAIOLForm form, SystemType curUserSystemType);
	
	//经销商第二类医疗器械经营备案凭证
	public DealerSTMDBRCForm selectDealerSTMDBRC(Long id);
	
	public int insertDealerSTMDBRC(Long id, AddDealerSTMDBRCForm form);
	
	public int updateDealerSTMDBRC(Long id, DealerSTMDBRCForm form, SystemType curUserSystemType);
}
