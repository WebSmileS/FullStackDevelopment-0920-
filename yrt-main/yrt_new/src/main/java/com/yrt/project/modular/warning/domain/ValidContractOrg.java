package com.yrt.project.modular.warning.domain;

import com.yrt.common.dict.SystemType;

import lombok.Data;

@Data
public class ValidContractOrg {

	private Long orgId;
	private String orgName;
	private SystemType systemType;
}
