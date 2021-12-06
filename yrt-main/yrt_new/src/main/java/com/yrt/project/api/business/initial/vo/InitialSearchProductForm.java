package com.yrt.project.api.business.initial.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InitialSearchProductForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "机构ID不能为空")
	private Long org_id;

	@ApiModelProperty(value="仓库ID",required=false)
	private Long warehouse_id;

	@ApiModelProperty(value="厂商名称",required=false)
	private String vendor_name;
	
	@ApiModelProperty(value="产品名称",required=false)
	private String product_name;
	
	@ApiModelProperty(value="状态 -1:-全部 0:未建账 1:已建账(默认全部)",required=false)
	private Integer status = -1;
}
