package com.yrt.project.modular.external.domain;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Org4DropDown implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="机构ID",required=false)
	private Long org_id;
	
	@ApiModelProperty(value="机构名称",required=false)
	private String name;
	
	@ApiModelProperty(value="机构统一社会信用代码",required=false)
	private String social_credit_code;
	
	@ApiModelProperty(value="机构类型",required=false)
	private Integer org_type;
}
