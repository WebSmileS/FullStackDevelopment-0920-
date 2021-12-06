package com.yrt.project.modular.organization.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class UserRole implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 角色内部编号 */
	private Long role_inner_sn;
	/** 用户内部编号 */
	private Long user_inner_sn;


}
