package com.yrt.project.modular.organization.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.SystemType;

import lombok.Data;

@Data
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** ID */
	private Long id;
	/** 系统类别 */
	private SystemType system_type;
	/** 注册机构ID */
	private Long org_id;
	/** 密码修改周期 */
	private Integer password_cycle;
	/** 状态 */
	private Integer status;
	/** 已删除 */
	private Integer is_delete;
	/** 注册时间 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date registered_time;
	/** 登录名称 */
	private String longin_name;
	/** 密码 */
	private String password;
	/** 登录电话 */
	private String phone;
	/** 用户类型 */
	private Integer type;

}
