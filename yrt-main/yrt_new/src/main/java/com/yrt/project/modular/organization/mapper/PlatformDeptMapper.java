package com.yrt.project.modular.organization.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.api.common.vo.SearchForm;
import com.yrt.project.api.platform.vo.organization.AddPlatformDeptForm;
import com.yrt.project.api.platform.vo.organization.EditPlatformDeptForm;
import com.yrt.project.modular.organization.domain.PlatformDept;
import com.yrt.project.modular.organization.domain.PlatformEmployee;

/**
 * 平台部门
 * @author leizhai
 *
 */

@Repository
public interface PlatformDeptMapper {
	/**
	 * 平台部门
	 */
	public PlatformDept selectPlatformDeptInfo(Long id);

	public List<PlatformDept> selectPlatformDeptChildrenList(long id);
	
	public List<PlatformDept> selectTopLevelPlatformDeptlist();

	public int insertPlatformDept(AddPlatformDeptForm platformDept);

	public int updatePlatformDept(EditPlatformDeptForm platformDept);

	public int deletePlatformDept(Long id);
	
	public int trueDeletePlatformDept(Long id);
	
	/**
	 * 平台员工
	 */
	
	public PlatformEmployee selectPlatformEmployeeInfoById(long id);
	
	public List<PlatformEmployee> selectPlatformEmployeeList(SearchForm form);
	
	public int insertPlatformEmployee(Map<String,Object> params);
	
	public int updatePlatformEmployee(Map<String,Object> params);
	
	public int deletePlatformEmployee(long id);
	
	public int deletePlatformDeptRelEmp(long id);
	
	public int insertPlatformDeptRelEmp(Map<String, Object> params);
	
	public List<PlatformEmployee>  selectTopPlatformEmployeeList();
	
	public List<PlatformEmployee> selectPlatformEmployeeChildrenList(long id);
	
}
