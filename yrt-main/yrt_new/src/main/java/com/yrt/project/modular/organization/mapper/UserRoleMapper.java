package com.yrt.project.modular.organization.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import com.yrt.project.modular.organization.domain.UserRole;

/**
 * 平台部门
 * @author leizhai
 *
 */

@Repository
public interface UserRoleMapper {
	

	public int insertUserRole(Map<String,Object> params);
	
	public int updateUserRole(Map<String,Object> params);
	
	public int deleteUserRole(Map<String,Object> params);
	
	public List<UserRole> selectUserRoleList(Map<String,Object> params);
	
	public UserRole selectUserRoleById(Long roleId);
	
}
