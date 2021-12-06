package com.yrt.project.modular.organization.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yrt.common.constant.Constants;
import com.yrt.common.constant.MessageConstant;
import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.Md5Utils;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.RandomPasswordsUtils;
import com.yrt.common.utils.ServletUtils;
import com.yrt.project.modular.organization.domain.User;
import com.yrt.project.modular.organization.mapper.UserMapper;
import com.yrt.project.modular.organization.service.IUserService;
import com.yrt.project.modular.platform.mapper.ProceduresMapper;

@Service
public class UserServiceImpl implements IUserService {
	
	@Autowired UserMapper mapper;
	
	@Autowired private IUserService service;
	@Autowired private ProceduresMapper proceduresMapper;

	@Override
	public int insertUser(User god) {
		return mapper.insertUser(god);
	}

	@Override
	public int updateUser(User god) {
		return mapper.updateUser(god);
	}

	@Override
	public int deleteUser(Long id) {
		return mapper.deleteUser(id);
	}
	
	@Override
	public int deleteUserUnscoped(Long id) {
		return mapper.deleteUserUnscoped(id);
	}

	@Override
	public User selectUserById(Long id) {
		return mapper.selectUserById(id);
	}

	@Override
	public List<User> selectUserListByOrgId(Long oid) {
		return mapper.selectUserListByOrgId(oid);
	}

	@Override
	public List<User> selectUserListByOrgId4Dropdown(int systemType, Long oid) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", systemType);
		params.put("id", oid);
		switch (systemType) {
		case 1:
			return mapper.selectUserRefEmpListByHospital4Dropdown(params);
		case 2:
			return mapper.selectUserRefEmpListByDealer4Dropdown(params);
		case 3:
			return mapper.selectUserRefEmpListByVendor4Dropdown(params);
		}
		return new ArrayList<>();
	}
	
	@Override
	public User selectUserByLoginName(String name ) {
		return mapper.selectUserByLoginName(name);		
	}
	
	@Override
	public long CreateUser(String employeeName, String phone, SystemType systemType, long org_id) {
		if (phone.length() <= 0) {
			throw new ValidateException(MessageUtils.message("param.not.null", "手机号码"));
		}
//		校验电话号码
//		PatternUtils.validatePhone(phone, "user.mobile.phone.number.not.valid");
//		按电话获取用户
		User u = this.selectUserByLoginName(phone);
		String password = RandomPasswordsUtils.randomPasswords(8);
		if (u == null) {
//			新建系统登录用户
			User newUser = new User();
			newUser.setLongin_name(phone);
			newUser.setPhone(phone);
			newUser.setOrg_id(org_id);
			newUser.setSystem_type(systemType);
			//newUser.setPassword(Md5Utils.hash(Constants.DEFAULT_USER_PASSWORD));
			newUser.setPassword(Md5Utils.hash(password));
			newUser.setPassword_cycle(0);
			newUser.setType(Constants.USER_TYPE_NORMAL);
			int result = this.insertUser(newUser);
			if (result == 0) {
				throw new ValidateException(MessageUtils.message("db.add.fail", "系统用户失败"));
			}
			//保存用户账号和密码
			User curUser = service.selectUserById(ServletUtils.getCurUserID());
			String curName = "";
            if(curUser != null) {
            	curName = curUser.getLongin_name();
            }
			Map<String, Object> params2 = new HashMap<>();
			params2.put("organization_name", employeeName);
			params2.put("account", phone);
			params2.put("password", password);
			params2.put("register_time", new Date());
			params2.put("operator", curName);
			params2.put("type", 1);//保存员工密码
			proceduresMapper.insertSMSTemp(params2);
			String phoneMsg="#useraccount#="+phone+"&#password#="+password;
			MessageUtils.sendMessage(phone, MessageConstant.TEMP_ACCOUNT_OPENING, phoneMsg);
			return newUser.getId();
		} else {
			throw new ValidateException(MessageUtils.message("user.mobile.phone.exists", "请先注销该号码"));
		}
	}
	
	@Override
	public int UpdateUserLoginName(long userID, String phone ) {
		User u = this.selectUserByLoginName(phone);
		if (u == null) {
			User updateUser = new User();
			updateUser.setId(userID);
			updateUser.setLongin_name(phone);
			updateUser.setPhone(phone);
			return this.updateUser(updateUser);
		}else {
			throw new ValidateException(MessageUtils.message("user.mobile.phone.exists", "请先注销该号码"));
		}
	}

}
