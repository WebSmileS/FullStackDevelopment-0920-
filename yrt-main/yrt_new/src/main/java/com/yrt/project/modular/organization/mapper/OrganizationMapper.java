package com.yrt.project.modular.organization.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.organization.domain.NotAuditOrganization;
import com.yrt.project.modular.organization.domain.OrgEmployee;

/**
 * 机构相关数据   数据层
 * @author Runner
 *
 */
@Repository
public interface OrganizationMapper {

	public NotAuditOrganization selectNoAuditOrgInfo(Long id);
	
	public List<NotAuditOrganization> selectAllNoAuditOrgList(Map<String,Object> params);
	
	public int insertNoAuditOrg(NotAuditOrganization org);
	
	public int updateNoAuditOrg(NotAuditOrganization org);
	
	public int deleteNoAuditOrg(Long id);
	
	public int updateStatus(NotAuditOrganization org);
	
	public OrgEmployee selectOrgEmployeeInfoByUserId(Long id);
}
