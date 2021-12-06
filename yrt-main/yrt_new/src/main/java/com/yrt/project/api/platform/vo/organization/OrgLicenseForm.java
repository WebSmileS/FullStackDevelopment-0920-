package com.yrt.project.api.platform.vo.organization;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.modular.organization.domain.OrgRelImage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 前端展示和修改机构营业执照信息
 * 
 * @author Runner
 *
 */
@Data
@ApiModel(value="前端展示和修改机构营业执照信息",description="前端展示和修改机构营业执照信息")
public class OrgLicenseForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private Map<String, Object> orgLicense = new HashMap<String, Object>();
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private Map<String, Object> orgInfo = new HashMap<String, Object>();
	
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private boolean isNew = false;
	
	@ApiModelProperty(value="机构信息名称",required=false)
	private String name;
	private String name_old;
	
	/** 统一社会信用代码 */
	@ApiModelProperty(value="机构信息统一社会信用代码",required=false)
	private String social_credit_code;
	private String social_credit_code_old;
	
	/** 企业类型 */
	@ApiModelProperty(value="机构信息企业类型",required=false)
	private Integer enterprise_type;
	private Integer enterprise_type_old;
	
	/** 法定代表人 */
	@ApiModelProperty(value="机构信息法定代表人",required=false)
	private String legal_representative;
	private String legal_representative_old;
	
	/** 注册资本 */
	@ApiModelProperty(value="机构信息注册资本",required=false)
	private String registered_capital;
	private String registered_capital_old;
	
	/** 地址 */
	@ApiModelProperty(value="机构信息地址",required=false)
	private String address;
	private String address_old;
	
	/** 成立日期 */
	@ApiModelProperty(value="机构信息成立日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date establish_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date establish_date_old;
	
	
	/** 开始日期 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty(value="营业执照开始时间",required=false)
	private Date begin_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date_old;
	
	/** 截止日期 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty(value="营业执照截止日期",required=false)
	private Date end_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date_old;
	
	/** 颁发日期 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty(value="营业执照颁发日期",required=false)
	private Date issue_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date issue_date_old;
	
	/** 登记机关 */
	@ApiModelProperty(value="营业执照登记机关",required=false)
	private String issue_organization;
	private String issue_organization_old;
	
	/** 经营范围 */
	@ApiModelProperty(value="营业执照经营范围",required=false)
	private String business_scope;
	private String business_scope_old;
	
	/** 相关图片 */
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private List<OrgRelImage> images;
	
	public boolean hasLicense() {
		if(StringUtils.isNotNull(begin_date) || StringUtils.isNotNull(begin_date_old)) {
			orgLicense.put("begin_date", begin_date);
		}
		if(StringUtils.isNotNull(end_date) || StringUtils.isNotNull(end_date_old)) {
			orgLicense.put("end_date", end_date);
		}
		if(StringUtils.isNotNull(issue_date) || StringUtils.isNotNull(issue_date_old)) {
			orgLicense.put("issue_date", issue_date);
		}
		if(StringUtils.isNotEmpty(issue_organization) || StringUtils.isNotEmpty(issue_organization_old)) {
			orgLicense.put("issue_organization", issue_organization);
		}
		if(StringUtils.isNotEmpty(business_scope) || StringUtils.isNotEmpty(business_scope_old)) {
			orgLicense.put("business_scope", business_scope);
		}
		return StringUtils.isNotEmpty(orgLicense);
	}
	
	public Map<String, Object> getOrgLicense(){
		return orgLicense;
	}
	
	public boolean hasOrgInfo() {
		if(StringUtils.isNotEmpty(name) || StringUtils.isNotEmpty(name_old)) {
			orgInfo.put("name", name);
		}
		if(StringUtils.isNotEmpty(social_credit_code) || StringUtils.isNotEmpty(social_credit_code_old)) {
			orgInfo.put("social_credit_code", social_credit_code);
		}
		if(StringUtils.isNotNull(enterprise_type) || StringUtils.isNotNull(enterprise_type_old)) {
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
