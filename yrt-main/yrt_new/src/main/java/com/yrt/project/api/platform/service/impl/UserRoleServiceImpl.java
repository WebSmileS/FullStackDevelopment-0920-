package com.yrt.project.api.platform.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.project.api.platform.service.UserRoleService;
import com.yrt.project.modular.organization.domain.UserRole;
import com.yrt.project.modular.organization.mapper.UserRoleMapper;

@Service
public class UserRoleServiceImpl implements UserRoleService{

	@Autowired
	private UserRoleMapper userRoleMapper;
	
	@Override
	@Transactional
	public int insertUserRole(Map<String, Object> params) {
		
		return userRoleMapper.insertUserRole(params);
	}

	@Override
	@Transactional
	public int updateUserRole(Map<String, Object> params) {

		return userRoleMapper.updateUserRole(params);
	}

	@Override
	@Transactional
	public int deleteUserRole(Map<String, Object> params) {
		
		return userRoleMapper.deleteUserRole(params);
	}

	@Override
	public List<UserRole> selectUserRoleList(Map<String, Object> params) {
		
		return userRoleMapper.selectUserRoleList(params);
	}

	@Override
	public UserRole selectUserRoleById(Long id) {
		return userRoleMapper.selectUserRoleById(id);
	}

}
