package com.yrt.project.modular.platform.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.common.domain.SmsTemp;

@Repository
public interface ProceduresMapper {

	//获取单据编号最大序号
	public String getMaxVoucherSn(Map<String,Object> params);
	
	//记录机构注册成功时的账号和密码
	public int insertSMSTemp(Map<String,Object> params);
	
	//查询登录帐号的初始密码
	public List<SmsTemp> selectInitLoginAccount(Map<String,Object> params);
	
}
