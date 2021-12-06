package com.yrt.project.api.common.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 前端通用搜索内容
 * @author Runner
 *
 */
@Data
@ApiModel(value="前端通用搜索内容", description="前端通用搜索内容")
public class SearchForm implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/** 名称* */
	@ApiModelProperty(value="名称",required=false)
	private String name = "";
	
	/** 是否认证通过 */
	@ApiModelProperty(value="是否只查询认证通过的机构，默认全部机构",required=false)
	private Boolean is_certifie = false;
	
}
