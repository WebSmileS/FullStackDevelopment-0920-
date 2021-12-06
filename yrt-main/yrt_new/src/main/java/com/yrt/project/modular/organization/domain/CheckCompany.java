package com.yrt.project.modular.organization.domain;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CheckCompany implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "系统中机构ID")
	@NotNull(message = "系统中机构ID不能为空")
	private Long id;
	
	@ApiModelProperty(value = "行政区划代码ID")
	private Long administrative_division_sn;
	
	@ApiModelProperty(value = "统一社会信用代码")
	@NotNull(message = "统一社会信用代码不能为空")
	private String social_credit_code;
	
	@ApiModelProperty(value = "注册时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date register_time;
	
	@ApiModelProperty(value = "名称")
	private String name;
	
	@ApiModelProperty(value = "别名(医院专有) ")
	private String alias;
	
	@ApiModelProperty(value = "地址")
	private String address;
	
	@ApiModelProperty(value = "电话")
	private String phone;
	
	@ApiModelProperty(value = "电子邮件")
	private String email;
	
	@ApiModelProperty(value = "描述")
	private String description;
}
