package com.yrt.project.api.business.applyvoucher.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AppApplySearchForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="机构ID",required=true)
	@NotNull(message = "机构ID不能为空")
	private Long oid;
	@ApiModelProperty(value="搜索关键字",required=false)
	private String key;
	
	@ApiModelProperty(value="状态传值可选: 0-全部 1-申领 中 2-全部发放 3-申领终止",required=true)
	@NotNull(message = "状态传值不能为空")
	private Integer status;
}
