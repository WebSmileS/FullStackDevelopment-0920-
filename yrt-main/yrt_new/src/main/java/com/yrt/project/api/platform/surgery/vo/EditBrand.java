package com.yrt.project.api.platform.surgery.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditBrand implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 品牌内部编号 */
	@ApiModelProperty(value="品牌内部编号",required=true)
	@NotNull(message = "品牌内部编号不能为空")
	private Long brand_inner_sn;
	/** 名称 */
	@ApiModelProperty(value="名称",required=true)
	@NotNull(message = "名称不能为空")
	private String name;
	/** 状态 */
	@ApiModelProperty(value="状态",required=true)
	@NotNull(message = "状态不能为空")
	private String status;
	
}
