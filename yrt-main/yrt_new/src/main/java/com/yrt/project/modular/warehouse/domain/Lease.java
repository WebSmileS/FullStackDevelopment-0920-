package com.yrt.project.modular.warehouse.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Lease {

	@ApiModelProperty(value = "租户ID")
	private Long id;
	
	@ApiModelProperty(value = "机构ID")
	private Long org_id;
	
	@ApiModelProperty(value = "经销商ID")
	private Long dealer_inner_sn;
	
	@ApiModelProperty(value = "经销商名称")
	private String dealer_name;
	
	@ApiModelProperty(value = "统一社会信用代码")
	private String social_credit_code;
	
	@ApiModelProperty(value = "电话")
	private String phone;
	
	@ApiModelProperty(value = "电子邮件")
	private String email;
	
	@ApiModelProperty(value = "注册时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date register_time;
}
