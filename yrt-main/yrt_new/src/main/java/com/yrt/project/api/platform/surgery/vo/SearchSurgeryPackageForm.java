package com.yrt.project.api.platform.surgery.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchSurgeryPackageForm implements Serializable {

private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="搜索机构名称",required=false)
	private String search_name;
	
	@ApiModelProperty(value="搜索机构类别",required=false)
	private Integer type_inner_sn;
	
	@ApiModelProperty(value="品牌类别",required=false)
	private Integer brand_inner_sn;
	
	@ApiModelProperty(value="骨科路径ID",required=false)
	private Long pid;
}
