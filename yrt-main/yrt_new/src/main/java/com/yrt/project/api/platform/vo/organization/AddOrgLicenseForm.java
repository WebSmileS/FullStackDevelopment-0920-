package com.yrt.project.api.platform.vo.organization;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 前端添加机构营业执照信息
 * 
 * @author Runner
 *
 */
@Data
public class AddOrgLicenseForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/*
	 * @ApiModelProperty(value="不需要前端输入",hidden=true) private Map<String, Object>
	 * orgLicense = new HashMap<String, Object>();
	 */
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private Map<String, Object> orgInfo = new HashMap<String, Object>();
	
	/** 名称* */
	@ApiModelProperty(value="机构信息名称",required=false)
	private String name;
	@ApiModelProperty(value="原机构信息名称",required=false)
	private String name_old;
	
	/** 统一社会信用代码 */
	@ApiModelProperty(value="机构信息统一社会信用代码",required=false)
	private String social_credit_code;
	@ApiModelProperty(value="原机构信息统一社会信用代码",required=false)
	private String social_credit_code_old;
	
	/** 企业类型 */
	@ApiModelProperty(value="机构信息企业类型",required=false)
	private String enterprise_type;
	@ApiModelProperty(value="原机构信息企业类型",required=false)
	private String enterprise_type_old;
	
	/** 法定代表人 */
	@ApiModelProperty(value="机构信息法定代表人",required=false)
	private String legal_representative;
	@ApiModelProperty(value="原机构信息法定代表人",required=false)
	private String legal_representative_old;
	
	/** 注册资本 */
	@ApiModelProperty(value="机构信息注册资本",required=false)
	private String registered_capital;
	@ApiModelProperty(value="原机构信息注册资本",required=false)
	private String registered_capital_old;
	
	/** 地址 */
	@ApiModelProperty(value="机构信息地址",required=false)
	private String address;
	@ApiModelProperty(value="原机构信息地址",required=false)
	private String address_old;
	
	/** 成立日期 */
	@ApiModelProperty(value="机构信息成立日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date establish_date;
	@ApiModelProperty(value="原机构信息成立日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date establish_date_old;
	
	
	/** 开始日期 */
	@ApiModelProperty(value="营业执照开始时间",required=true)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message="开始日期不能为空")
	private Date begin_date;
	
	/** 截止日期 */
	@ApiModelProperty(value="营业执照截止日期",required=true)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message="截止日期不能为空")
	private Date end_date;
	
	/** 颁发日期 */
	@ApiModelProperty(value="营业执照颁发日期",required=true)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message="颁发日期不能为空")
	private Date issue_date;
	
	/** 登记机关 */
	@ApiModelProperty(value="营业执照登记机关",required=false)
	private String issue_organization = "";
	
	
	/** 经营范围 */
	@ApiModelProperty(value="营业执照经营范围",required=false)
	private String business_scope = "";
	
	/*
	 * public boolean hasLicense() { if(StringUtils.isNotNull(begin_date)) {
	 * orgLicense.put("begin_date", begin_date); }
	 * if(StringUtils.isNotNull(end_date)) { orgLicense.put("end_date", end_date); }
	 * if(StringUtils.isNotNull(issue_date)) { orgLicense.put("issue_date",
	 * issue_date); } if(StringUtils.isNotNull(issue_organization)) {
	 * orgLicense.put("issue_organization", issue_organization); }
	 * if(StringUtils.isNotNull(business_scope)) { orgLicense.put("business_scope",
	 * business_scope); } return StringUtils.isNotEmpty(orgLicense); }
	 * 
	 * public Map<String, Object> getOrgLicense(){ return orgLicense; }
	 */
	
	public boolean hasOrgInfo() {
		if(StringUtils.isNotEmpty(name)) {
			orgInfo.put("name", name);
		}
		if(StringUtils.isNotEmpty(social_credit_code) || StringUtils.isNotEmpty(social_credit_code_old)) {
			if(StringUtils.isEmpty(social_credit_code)) {
				throw new ValidateException(MessageUtils.message("param.not.null", "统一社会信用代码"));
			}
			if(social_credit_code.trim().length() > 18) {
				throw new ValidateException(MessageUtils.message("length.not.valid", "统一社会信用代码",1,18));
			}
			orgInfo.put("social_credit_code", social_credit_code);
		}
		if(StringUtils.isNotEmpty(enterprise_type)) {
			if(StringUtils.isNull(enterprise_type)) {
				throw new ValidateException(MessageUtils.message("param.not.null", "企业类型"));
			}
			orgInfo.put("enterprise_type", enterprise_type);
		}
		if(StringUtils.isNotEmpty(legal_representative) || StringUtils.isNotEmpty(legal_representative_old)) {
			if(StringUtils.isEmpty(legal_representative)) {
				legal_representative = "";
			}
			orgInfo.put("legal_representative", legal_representative);
		}
		if(StringUtils.isNotEmpty(registered_capital) || StringUtils.isNotEmpty(registered_capital_old)) {
			if(StringUtils.isEmpty(registered_capital)) {
				registered_capital = "";
			}
			orgInfo.put("registered_capital", registered_capital);
		}
		if(StringUtils.isNotEmpty(address) || StringUtils.isNotEmpty(address_old)) {
			if(StringUtils.isEmpty(address)) {
				address = "";
			}
			orgInfo.put("address", address);
		}
		if(StringUtils.isNotNull(establish_date) || StringUtils.isNotNull(establish_date_old)) {
			if(StringUtils.isNull(establish_date)) {
				orgInfo.put("establish_date_null", 1);
			}
			orgInfo.put("establish_date", establish_date);
		}
		return StringUtils.isNotEmpty(orgInfo);
	}

	public Map<String, Object> getOrgInfo(){
		return orgInfo;
	}
}
