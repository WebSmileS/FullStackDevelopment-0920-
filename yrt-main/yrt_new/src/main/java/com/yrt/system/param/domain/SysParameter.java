package com.yrt.system.param.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.yrt.common.dict.SystemType;

import lombok.Data;

@Data
public class SysParameter {
	
	public static SysParameter of(SystemType systemType, Long organizationId, String paramKey, String paramName, String paramValue) {
		SysParameter ret = new SysParameter();
		ret.setSystemType(systemType);
		ret.setOrganizationId(organizationId);
		ret.setParamKey(paramKey);
		ret.setParamName(paramName);
		ret.setParamValue(paramValue);
		return ret;
	}
	
	public static SysParameter of(SysParameter src) {
		return of(src.getSystemType(), src.getOrganizationId(), src.getParamKey(), src.getParamName(), src.getParamValue());
	}
	
	@NotNull
	private SystemType systemType;
	@NotNull
	private Long organizationId;
	@NotBlank
	private String paramKey;
	@NotBlank
	private String paramName;
	@NotBlank
	private String paramValue;
}
