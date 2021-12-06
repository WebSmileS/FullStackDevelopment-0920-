package com.yrt.project.modular.organization.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Company implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "公司内部编号ID")
	private Long id;
	
	@ApiModelProperty(value = "审核员工内部编号")
	private Long certifie_employee_inner_sn;
	
	@ApiModelProperty(value = "审核员工名称")
	private String certifie_employee_name;
	
	@ApiModelProperty(value = "行政区划代码ID")
	private Long administrative_division_sn;
	
	@ApiModelProperty(value = "状态 0-禁用  1-正常")
	private Integer status = 1;
	
	/** 已删除 0-可用 1-删除 */
//	private Integer is_delete = 0;
	
	@ApiModelProperty(value = "数据来源，用于分别是否平台建立 0-平台建立 1-非平台建立")
	private Integer data_source = 0;
	
	@ApiModelProperty(value = "机构认证状态 0-未认证 1-用户申请认证 2-认证通过 3-认证未通过")
	private Integer certification_status = 0;
	
	@ApiModelProperty(value = "企业类型")
	private Integer enterprise_type;
	
	@ApiModelProperty(value = "成立日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date establish_date;
	
	@ApiModelProperty(value = "注册时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date register_time = new Date();
	
	@ApiModelProperty(value = "认证时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date certifie_time;
	
	@ApiModelProperty(value = "统一社会信用代码")
	private String social_credit_code;
	
	@ApiModelProperty(value = "名称")
	private String name = "";
	
	@ApiModelProperty(value = "别名(医院专有) ")
	private String alias = "";
	
	@ApiModelProperty(value = "法定代表人")
	private String legal_representative="";
	
	@ApiModelProperty(value = "企业负责人")
	private String principal="";
	
	@ApiModelProperty(value = "注册资本")
	private String registered_capital="";
	
	@ApiModelProperty(value = "地址")
	private String address = "";
	
	@ApiModelProperty(value = "银行名称")
	private String bank_name = "";
	
	@ApiModelProperty(value = "银行账号")
	private String bank_account = "";
	
	@ApiModelProperty(value = "电话")
	private String phone = "";
	
	@ApiModelProperty(value = "电子邮件")
	private String email = "";
	
	@ApiModelProperty(value = "描述")
	private String description = "";

}
