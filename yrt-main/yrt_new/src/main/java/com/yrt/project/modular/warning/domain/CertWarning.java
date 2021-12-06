package com.yrt.project.modular.warning.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.SystemType;
import com.yrt.common.utils.Utils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CertWarning implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "唯一ID")
	private Long id;
	@ApiModelProperty(value = "创建预警信息机构唯一ID(平台用0)")
	private Long created_org_id;
	@ApiModelProperty(value = "创建预警信息机构类型(PLATFORM-平台 HOSPITAL-医院 DEALER-经销商 VENDOR-厂商)")
	private SystemType created_system_type;
	@ApiModelProperty(value = "过期日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date expiration_date;
	@ApiModelProperty(value = "预警机构ID")
	private Long organization_inner_sn;
	@ApiModelProperty(value = "预警机构名称")
	private String organization_name;
	@ApiModelProperty(value = "预警信息机构类型(PLATFORM-平台 HOSPITAL-医院 DEALER-经销商 VENDOR-厂商)")
	private SystemType system_type;
	@ApiModelProperty(value = "资证名称")
	private String certification_name;
	@ApiModelProperty(value = "统一社会信用代码")
	private String social_credit_code;
	
	@ApiModelProperty(value = "电话")
	private String phone;
	@ApiModelProperty(value = "邮箱")
	private String email;
	
	public void trim() {
		phone = Utils.isEmpty(phone)?"":phone;
		email = Utils.isEmpty(email)?"":email;
	}
}
