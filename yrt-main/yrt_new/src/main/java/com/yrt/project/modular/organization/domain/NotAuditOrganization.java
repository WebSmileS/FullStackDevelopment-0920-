package com.yrt.project.modular.organization.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.SystemType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class NotAuditOrganization implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "注册待审核机构内部编号ID")
	private Long not_audit_organization_inner_sn;
	@ApiModelProperty(value = "行政区划ID")
	private Long administrative_division_sn;
	@ApiModelProperty(value = "系统类型")
	private SystemType system_type;
	@ApiModelProperty(value = "注册状态 0-待审核 1-审核失败")
	private Integer audit_status;
	@ApiModelProperty(value = "注册时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date register_time;
	@ApiModelProperty(value = "名称")
	private String name;
	@ApiModelProperty(value = "别名")
	private String alias;
	@ApiModelProperty(value = "地址")
	private String address;
	@ApiModelProperty(value = "注册人")
	private String registrant;
	@ApiModelProperty(value = "注册人电话")
	private String registrant_phone;
	@ApiModelProperty(value = "注册人电子邮件")
	private String registrant_email;
	@ApiModelProperty(value = "描述")
	private String description;
	@ApiModelProperty(value = "统一社会信用代码")
	private String social_credit_code;

}
