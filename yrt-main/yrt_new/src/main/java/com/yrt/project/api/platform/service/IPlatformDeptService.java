package com.yrt.project.api.platform.service;

import java.util.List;

import com.yrt.project.api.common.vo.SearchForm;
import com.yrt.project.api.platform.vo.organization.AddPlatformDeptForm;
import com.yrt.project.api.platform.vo.organization.AddPlatformEmployeeForm;
import com.yrt.project.api.platform.vo.organization.EditPlatformDeptForm;
import com.yrt.project.api.platform.vo.organization.EditPlatformEmployeeForm;
import com.yrt.project.modular.organization.domain.PlatformDept;
import com.yrt.project.modular.organization.domain.PlatformEmployee;

public interface IPlatformDeptService {
	/**
	 * 平台部门部分
	 */
	public PlatformDept selectPlatformDeptInfo(Long id);

	public List<PlatformDept> selectPlatformDeptChildrenList(long id);

	public long insertPlatformDept(AddPlatformDeptForm PlatformDept);

	public int updatePlatformDept(EditPlatformDeptForm PlatformDept);

	public int deletePlatformDept(Long id);
	
	public List<PlatformDept> selectTopLevelPlatformDept();
	
 	public List<PlatformDept> selectTreePlatformDeptList();
 	
	/**
	 * 平台员工部分
	 */
 	
	public PlatformEmployee selectPlatformEmployeeInfoById(long id);
	
	public List<PlatformEmployee> selectPlatformEmployeeList(SearchForm form);
	
	public List<PlatformEmployee> selectPlatformEmployeeDropdownList();
	
	public long insertPlatformEmployee(AddPlatformEmployeeForm platformEmployee);
	
	public int updatePlatformEmployee(EditPlatformEmployeeForm platformEmployee);
	
	public int deletePlatformEmployee(long id);
	
	public List<PlatformEmployee>  selectTreePlatformEmployeeList();
	//角色相关操作
//	public UserRole getUserRole(Long id);
	
}
