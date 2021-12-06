package com.yrt.project.api.platform.surgery.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddOrthopaedicPathForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 上级手术径路内部编号  */
	@ApiModelProperty(value="上级手术径路ID",required=false)
	private Long parent_op_inner_sn;
	/** 名称  */
	@ApiModelProperty(value="名称",required=false)
	@NotBlank(message = "名称不能为空")
	private String name;
	
}
