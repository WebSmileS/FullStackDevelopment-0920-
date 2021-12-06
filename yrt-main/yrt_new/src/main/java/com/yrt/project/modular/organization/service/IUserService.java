package com.yrt.project.modular.organization.service;

import java.util.List;

import com.yrt.common.dict.SystemType;
import com.yrt.project.modular.organization.domain.User;

public interface IUserService {

	public int insertUser(User god);
	
	public int updateUser(User god);
	
	public int deleteUser(Long id);
	
	public int deleteUserUnscoped(Long id);
	
	public User selectUserById(Long id);
	
	public User selectUserByLoginName(String name );
	
	public List<User> selectUserListByOrgId(Long oid);
	
	public List<User> selectUserListByOrgId4Dropdown(int systemType, Long oid);
	
	long CreateUser(String employeeName, String phone, SystemType systemType, long org_id);

	int UpdateUserLoginName(long userID, String phone);
}
