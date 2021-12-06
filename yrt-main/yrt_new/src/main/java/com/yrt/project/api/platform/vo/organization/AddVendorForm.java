package com.yrt.project.api.platform.vo.organization;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddVendorForm implements Serializable {

	private static final long serialVersionUID = 1L;

	
	/** 行政区划代码ID */
	@ApiModelProperty(value="行政区划代码ID",required=true)
	@NotNull(message = "行政区划不能为空")
	private Long administrative_division_sn;
	
	/** 企业类型 */
	@ApiModelProperty(value="企业类型",required=true)
	@NotNull(message = "企业类型不能为空")
	private Integer enterprise_type;
	
	/** 成立日期 */
	@ApiModelProperty(value="成立日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date establish_date;
	
	/** 统一社会信用代码 */
	@ApiModelProperty(value="统一社会信用代码",required=true)
	@NotBlank(message = "统一社会信用代码不能为空")
	private String social_credit_code;
	
	/** 名称* */
	@ApiModelProperty(value="名称",required=true)
	@NotBlank(message = "名称不能为空")
	private String name;
	
	/** 法定代表人 */
	@ApiModelProperty(value="法定代表人",required=false)
	private String legal_representative = "";
	
	/** 企业负责人 */
	@ApiModelProperty(value="企业负责人",required=false)
	private String principal = "";
	
	/** 注册资本 */
	@ApiModelProperty(value="注册资本",required=false)
	private String registered_capital = "";
	
	/** 地址 */
	@ApiModelProperty(value="地址",required=false)
	private String address = "";
	
	/** 银行名称 */
	@ApiModelProperty(value="银行名称",required=false)
	private String bank_name = "";
	
	/** 银行账号 */
	@ApiModelProperty(value="银行账号",required=false)
	private String bank_account = "";
	
	/** 电话 */
	@ApiModelProperty(value="电话",required=false)
	private String phone = "";
	
	/** 电子邮件 */
	@ApiModelProperty(value="电子邮件",required=false)
	private String email = "";
	
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description = "";
	
}
