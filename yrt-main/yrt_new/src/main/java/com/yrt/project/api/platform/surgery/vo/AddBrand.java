package com.yrt.project.api.platform.surgery.vo;

import java.io.Serializable;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddBrand implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 名称 */
	@ApiModelProperty(value="名称",required=true)
	@NotNull(message = "名称不能为空")
	private String name;
	/** 状态 */
	@ApiModelProperty(value="状态",required=true)
	@NotNull(message = "状态不能为空")
	private String status = "";
	
}
