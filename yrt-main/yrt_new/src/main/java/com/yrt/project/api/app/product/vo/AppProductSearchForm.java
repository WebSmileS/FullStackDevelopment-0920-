package com.yrt.project.api.app.product.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AppProductSearchForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="手机搜索关键字",required=false)
	private String key;
	
	@ApiModelProperty(value="当前机构ID",required=false)
	private Long org_id;

}
