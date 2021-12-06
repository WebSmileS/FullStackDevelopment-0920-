package com.yrt.project.api.platform.service;

import java.util.List;

import com.yrt.common.dict.SystemType;
import com.yrt.project.api.common.vo.AddNoAuditOrgForm;
import com.yrt.project.api.platform.vo.organization.MatchResult;
import com.yrt.project.api.platform.vo.organization.NoAuditOrgForm;
import com.yrt.project.api.platform.vo.organization.SearchOrgForm;
import com.yrt.project.modular.organization.domain.CheckCompany;
import com.yrt.project.modular.organization.domain.NotAuditOrganization;


public interface IOrgRegisterAuditService {

	
	/**
	 * 更具id查询
	 */
	public NotAuditOrganization selectNoAuditOrgInfo(Long id);
	
	/**
	 * 根据类型查询表格
	 */
	public List<NotAuditOrganization> selectAllNoAuditOrgList(SystemType systemType,SearchOrgForm form);
	
	/**
	 * 修改
	 */
	public int updateNoAuditOrg(Long id, NoAuditOrgForm form);
	
	/**
	 * 删除
	 */
	public int deleteNoAuditOrg(Long id);
	
	/**
	 * 修改转态为1 不通过
	 */
	public int noPassAduitOrg(Long id, String reason); 
	/**
	 * 修改状态为0 通过
	 */
	public MatchResult passAduitOrg(Long id);
	
	public int match(CheckCompany company, Long id);

	public int insertNoAuditOrg(AddNoAuditOrgForm form);

}
