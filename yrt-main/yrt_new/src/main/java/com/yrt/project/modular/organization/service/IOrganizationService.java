package com.yrt.project.modular.organization.service;

import java.util.List;

import com.yrt.common.dict.SystemType;
import com.yrt.project.modular.organization.domain.NotAuditOrganization;
import com.yrt.project.modular.organization.domain.SearchForm;

public interface IOrganizationService {

public NotAuditOrganization selectNoAuditOrgInfo(Long id);
	
	public List<NotAuditOrganization> selectAllNoAuditOrgList(SystemType systemType,SearchForm form);
	
	public int insertNoAuditOrg(NotAuditOrganization org);
	
	public int updateNoAuditOrg(NotAuditOrganization org);
	
	public int deleteNoAuditOrg(Long id);
	
	public int updateStatus(NotAuditOrganization org);
	
	public int passAduitOrg(Long id);
	
}
