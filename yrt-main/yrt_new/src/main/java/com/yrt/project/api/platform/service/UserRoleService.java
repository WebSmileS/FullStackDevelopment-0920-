package com.yrt.project.api.platform.service;

import java.util.List;
import java.util.Map;
import com.yrt.project.modular.organization.domain.UserRole;

public interface UserRoleService {
	
	public int insertUserRole(Map<String,Object> params);
	
	public int updateUserRole(Map<String,Object> params);
	
	public int deleteUserRole(Map<String,Object> params);
	
	public List<UserRole> selectUserRoleList(Map<String,Object> params);
	
	public UserRole selectUserRoleById(Long id);
}
