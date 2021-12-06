package com.yrt.project.modular.common.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SmsTemp {

	@ApiModelProperty(value = "帐号名称")
	private String organization_name;
	@ApiModelProperty(value = "帐号/电话")
	private String account;
	@ApiModelProperty(value = "初始密码")
	private String password;
	@ApiModelProperty(value = "注册时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date register_time;
	@ApiModelProperty(value = "操作人")
	private String operator;
	@ApiModelProperty(value = "类型(默认1) 0-机构管理员密码 1-员工密码")
	private Integer type;
}
