package com.yrt.project.api.common.vo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchAccountForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "帐号名称")
	private String organization_name;
	@ApiModelProperty(value = "帐号")
	private String account;
	@ApiModelProperty(value = "类型(默认1) 0-机构管理员密码 1-员工密码")
	private Integer type = 1;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
}
