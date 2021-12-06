package com.yrt.project.api.platform.surgery.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class KitIDs implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="工具包ID",required=true)
	@NotNull(message = "工具包ID不能为空")
	private Long kit_inner_sn;
	
	@ApiModelProperty(value="排序号",required=true)
	@NotNull(message = "排序号不能为空")
	private Integer sort_number;
}
