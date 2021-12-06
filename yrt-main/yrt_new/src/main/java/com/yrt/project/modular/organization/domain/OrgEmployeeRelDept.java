package com.yrt.project.modular.organization.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class OrgEmployeeRelDept implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long employee_inner_sn;
	private Long department_inner_sn;

}
