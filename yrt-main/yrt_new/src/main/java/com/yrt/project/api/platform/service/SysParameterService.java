package com.yrt.project.api.platform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yrt.common.dict.SystemType;
import com.yrt.common.utils.Utils;
import com.yrt.system.param.domain.SysParameter;
import com.yrt.system.param.mapper.SysParameterMapper;

@Service
public class SysParameterService {

	public static String SINGLE_USE = "SINGLE_USE";// 机构独立使用，包括订单自动确认和依赖采购订单调单入库
	public static String WARNING = "WARNING";// 预警设置
	public static String GSP_SETUP = "GSP_SETUP";// GSP设置
	public static String INIT_PURCHASING_RECORD = "INIT_PURCHASING_RECORD";// 从已有采购订单初始化采购记录

	@Autowired
	SysParameterMapper mapper;

	public boolean isSingle(SystemType systemType, long organizationId) {
		String ret = mapper.getCurOrgSystParamValue(systemType, organizationId, SINGLE_USE);
		if (Utils.isNotEmpty(ret)) {
			return ret.equals("0") ? false : true;
		}
		return false;
	}
	
	public String getWarningSetup(SystemType systemType, long organizationId) {
		String ret = mapper.getCurOrgSystParamValue(systemType, organizationId, WARNING);
		if(Utils.isEmpty(ret)) {
			ret = getDefaultWarningSetup();
		}
		return ret;
	}
	
	public String getDefaultWarningSetup() {
		return mapper.getCurOrgSystParamValue(SystemType.PLATFORM, 0L, WARNING);
	}
	
	public String getDefaultGSPSetup() {
		return mapper.getCurOrgSystParamValue(SystemType.PLATFORM, 0L, GSP_SETUP);
	}
	
	public String getGSPSetup(SystemType systemType, long orgId) {
		String ret = mapper.getCurOrgSystParamValue(systemType, orgId, GSP_SETUP);
		if(Utils.isEmpty(ret)) {
			ret = getDefaultGSPSetup();
		}
		return ret;
	}
	
	public boolean isInitPurchasingRecord() {
		boolean isInitPurchasingRecord = true;
		String ret = mapper.getCurOrgSystParamValue(SystemType.PLATFORM, 0L, INIT_PURCHASING_RECORD);
		if (Utils.isNotEmpty(ret)) {
			isInitPurchasingRecord = ret.equals("0") ? false : true;
		}
		
		return isInitPurchasingRecord;
	}
	
	public void saveParameter(SystemType systemType, long organizationId, String paramKey, String paramName, String paramValue) {
		SysParameter param = SysParameter.of(systemType, organizationId, paramKey, paramName, paramValue);
		mapper.insertOrUpdateSysParameter(param);
	}
}
