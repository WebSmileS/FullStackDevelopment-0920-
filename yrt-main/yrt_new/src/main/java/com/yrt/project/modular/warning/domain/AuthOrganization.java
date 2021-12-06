package com.yrt.project.modular.warning.domain;

import com.yrt.common.dict.SystemType;

import lombok.Data;

@Data
public class AuthOrganization {

	private Long orgId;
	private String orgName;
	private SystemType systemType;
	
	private String social_credit_code;
	private String phone;
	private String email;
}
