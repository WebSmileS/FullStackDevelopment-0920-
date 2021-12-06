package com.yrt.project.api.platform.vo.organization;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditOrgInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private Map<String, Object> info = new HashMap<String, Object>();
	
	/** 行政区划代码ID */
	@ApiModelProperty(value="行政区划代码ID",required=false)
	private Long administrative_division_sn;
	private Long administrative_division_sn_old;
	
	/** 企业类型 */
	@ApiModelProperty(value="企业类型",required=false)
	private Integer enterprise_type;
	private Integer enterprise_type_old;
	
	/** 成立日期 */
	@ApiModelProperty(value="成立日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date establish_date;
	private Date establish_date_old;
	
	/** 统一社会信用代码 */
	@ApiModelProperty(value="统一社会信用代码",required=false)
	private String social_credit_code;
	private String social_credit_code_old;
	
	/** 名称* */
	@ApiModelProperty(value="名称",required=false)
	private String name;
	private String name_old;
	
	/** 别名 */
	@ApiModelProperty(value="别名",required=false)
	private String alias;
	private String alias_old;
	
	/** 法定代表人 */
	@ApiModelProperty(value="法定代表人",required=false)
	private String legal_representative;
	private String legal_representative_old;
	
	/** 企业负责人 */
	@ApiModelProperty(value="企业负责人",required=false)
	private String principal;
	private String principal_old;
	
	/** 注册资本 */
	@ApiModelProperty(value="注册资本",required=false)
	private String registered_capital;
	private String registered_capital_old;
	
	/** 地址 */
	@ApiModelProperty(value="地址",required=false)
	private String address;
	private String address_old;
	
	/** 银行名称 */
	@ApiModelProperty(value="银行名称",required=false)
	private String bank_name;
	private String bank_name_old;
	
	/** 银行账号 */
	@ApiModelProperty(value="银行账号",required=false)
	private String bank_account;
	private String bank_account_old;
	
	/** 电话 */
	@ApiModelProperty(value="电话",required=false)
	private String phone;
	private String phone_old;
	
	/** 电子邮件 */
	@ApiModelProperty(value="电子邮件",required=false)
	private String email;
	private String email_old;
	
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description;
	private String description_old;
	
	public boolean hasUpdate() {
		if(StringUtils.isNotNull(administrative_division_sn) || StringUtils.isNotNull(administrative_division_sn_old)) {
			info.put("administrative_division_sn", administrative_division_sn);
		}
		if(StringUtils.isNotNull(enterprise_type) || StringUtils.isNotNull(enterprise_type_old)) {
			if(StringUtils.isNull(enterprise_type)) {
				throw new ValidateException(MessageUtils.message("param.not.null", "企业类型"));
			}
			info.put("enterprise_type", enterprise_type);
		}
		if(StringUtils.isNotNull(establish_date) || StringUtils.isNotNull(establish_date_old)) {
			if(StringUtils.isNull(establish_date)) {
				info.put("establish_date_null", 1);
			}
			info.put("establish_date", establish_date);
		}
		if(StringUtils.isNotEmpty(name) || StringUtils.isNotEmpty(name_old)) {
			info.put("name", name);
		}
		if(StringUtils.isNotEmpty(alias) || StringUtils.isNotEmpty(alias_old)) {
			if(StringUtils.isEmpty(alias)) {
				alias = "";
			}
			info.put("alias", alias);
		}
		if(StringUtils.isNotEmpty(legal_representative) || StringUtils.isNotEmpty(legal_representative_old)) {
			if(StringUtils.isEmpty(legal_representative)) {
				legal_representative = "";
			}
			info.put("legal_representative", legal_representative);
		}
		if(StringUtils.isNotEmpty(principal) || StringUtils.isNotEmpty(principal_old)) {
			if(StringUtils.isEmpty(principal)) {
				principal = "";
			}
			info.put("principal", principal);
		}
		if(StringUtils.isNotEmpty(address) || StringUtils.isNotEmpty(address_old)) {
			if(StringUtils.isEmpty(address)) {
				address = "";
			}
			info.put("address", address);
		}
		if(StringUtils.isNotEmpty(registered_capital) || StringUtils.isNotEmpty(registered_capital_old)) {
			if(StringUtils.isEmpty(registered_capital)) {
				registered_capital = "";
			}
			info.put("registered_capital", registered_capital);
		}
		if(StringUtils.isNotEmpty(bank_name) || StringUtils.isNotEmpty(bank_name_old)) {
			if(StringUtils.isEmpty(bank_name)) {
				bank_name = "";
			}
			info.put("bank_name", bank_name);
		}
		if(StringUtils.isNotEmpty(bank_account) || StringUtils.isNotEmpty(bank_account_old)) {
			if(StringUtils.isEmpty(bank_account)) {
				bank_account = "";
			}
			info.put("bank_account", bank_account);
		}
		if(StringUtils.isNotEmpty(email) || StringUtils.isNotEmpty(email_old)) {
			if(StringUtils.isEmpty(email)) {
				email = "";
			}
			info.put("email", email);
		}
		if(StringUtils.isNotEmpty(phone) || StringUtils.isNotEmpty(phone_old)) {
			info.put("phone", phone);
		}
		if(StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(description_old)) {
			if(StringUtils.isEmpty(description)) {
				description = "";
			}
			info.put("description", description);
		}
		if(StringUtils.isNotEmpty(social_credit_code) || StringUtils.isNotEmpty(social_credit_code_old)) {
			if(StringUtils.isEmpty(social_credit_code)) {
				throw new ValidateException(MessageUtils.message("param.not.null", "统一社会信用代码"));
			}
			if(social_credit_code.trim().length() > 18) {
				throw new ValidateException(MessageUtils.message("length.not.valid", "统一社会信用代码",1,18));
			}
			info.put("social_credit_code", social_credit_code);
		}
		return StringUtils.isNotEmpty(info);
	}
	
	public Map<String, Object> getOrgInfo(){
		return info;
	}
}
