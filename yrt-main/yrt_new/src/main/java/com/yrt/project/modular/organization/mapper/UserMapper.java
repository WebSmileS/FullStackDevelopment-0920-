 package com.yrt.project.modular.organization.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.organization.domain.User;

@Repository
public interface UserMapper {

	public int insertUser(User god);
	
	public int updateUser(User god);
	
	public int deleteUser(Long id);
	
	public int deleteUserUnscoped(Long id);
	
	public User selectUserById(Long id);
	
	public User selectUserByLoginName(String name);
	
	public List<User> selectUserListByOrgId(Long id);
	
	public List<User> selectUserListByOrgId4Dropdown(Map<String, Object> params);
	
	public List<User> selectUserRefEmpListByHospital4Dropdown(Map<String, Object> params);
	public List<User> selectUserRefEmpListByDealer4Dropdown(Map<String, Object> params);
	public List<User> selectUserRefEmpListByVendor4Dropdown(Map<String, Object> params);
}
