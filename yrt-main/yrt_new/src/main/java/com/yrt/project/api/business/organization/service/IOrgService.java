package com.yrt.project.api.business.organization.service;

import java.util.List;
import java.util.Map;

import com.yrt.common.dict.SystemType;
import com.yrt.project.api.business.organization.vo.AddOrgDeptForm;
import com.yrt.project.api.business.organization.vo.AddOrgEmployeeForm;
import com.yrt.project.api.business.organization.vo.EditOrgDeptForm;
import com.yrt.project.api.business.organization.vo.EditOrgEmployeeForm;
import com.yrt.project.api.common.vo.SearchForm;
import com.yrt.project.modular.organization.domain.OrgDept;
import com.yrt.project.modular.organization.domain.OrgEmployee;

public interface IOrgService {
	
	//机构部门管理
	public OrgDept insertOrgDept(SystemType systemType, Long id, AddOrgDeptForm form);
	
	public int updateOrgDept(SystemType systemType, Long id, Long did, EditOrgDeptForm form);
	
	public int deleteOrgDept(SystemType systemType, Long id, Long did);
	
	public OrgDept selectOrgDeptInfoById(SystemType systemType, Long id, Long did);
	
	public List<OrgDept> selectTopLevelOrgDeptListByOrgId(SystemType systemType, Long id);
	
	public List<OrgDept> selectOrgDeptListByPid(SystemType systemType, Long id, Long pid);
	
	public List<OrgDept> selectTreeOrgDeptListByOrgId(SystemType systemType, Long id);
	
	public void selectOrgDeptParentsById(OrgDept dept, SystemType systemType, Long id, Long pid);
	
	//机构人员管理
	public OrgEmployee insertOrgEmployee(SystemType systemType, Long id, AddOrgEmployeeForm form);
	
	public int updateOrgEmployee(SystemType systemType, Long id, Long eid, EditOrgEmployeeForm form);
	
	public int deleteOrgEmployee(SystemType systemType, Long id, Long eid);
	
	public OrgEmployee selectOrgEmployeeInfoById(SystemType systemType, Long id, Long eid);
	
	public List<OrgEmployee> selectOrgEmployeeListByOrgId(SystemType systemType, Long id);
	
	public List<OrgEmployee> selectOrgEmployeeListByOrgId(SystemType systemType, Long id, SearchForm form);

	public List<OrgEmployee> selectTreeOrgEmpListByOrgId(SystemType systemType, Long id);

	public List<Map<String,Object>> selectRelDeptList(SystemType systemType, Long id, Long curUserId);
	
	/**
	 * 根用户账号id 取 雇员信息 (平台账号为null)
	 * @param systemType 
	 * @param userId user account(god)
	 * @return null if systemType=PLATFORM
	 */
	OrgEmployee getEmployeeInfoByUser(SystemType systemType, long userId);
		
}
