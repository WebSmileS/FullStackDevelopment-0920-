package com.yrt.project.api.business.certificate.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CertSearchOrgForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="机构ID",required=true)
	private Long oid;
	
	/** 统一社会信用代码 */
	@ApiModelProperty(value="统一社会信用代码",required=false)
	private String social_credit_code;

	/** 名称* */
	@ApiModelProperty(value="名称",required=false)
	private String name;
	
	/** 行政区划代码 */
	@ApiModelProperty(value="行政区划代码",required=false)
	private Long administrative_division_sn;

}
