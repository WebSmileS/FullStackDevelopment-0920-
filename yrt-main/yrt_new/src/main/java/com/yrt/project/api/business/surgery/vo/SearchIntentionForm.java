package com.yrt.project.api.business.surgery.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchIntentionForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="搜索机构名称",required=false)
	private String search_name;
	
	@ApiModelProperty(value="搜索机构别名",required=false)
	private String search_alias;
}
