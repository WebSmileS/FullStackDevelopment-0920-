package com.yrt.project.api.common.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="前端登录信息", description="前端登录信息")
public class LoginForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="登录账户:登录名或电话",required=true)
	@NotBlank(message= "登录账户不能为空")
	private String account;
	@ApiModelProperty(value="登录密码",required=true)
	@NotBlank(message= "登录密码不能为空")
	private String password;
}
