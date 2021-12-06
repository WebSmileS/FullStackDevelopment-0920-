package com.yrt.project.api.platform.surgery.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddOrthopaedicPackageToPathForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="骨科路径ID",required=true)
	@NotNull(message = "骨科路径ID不能为空")
	private Long op_inner_sn;
	
	@ApiModelProperty(value="手术包ID,多个ID用英文逗号','分隔",required=true)
	@NotBlank(message = "手术包ID不能为空")
	private String packages;

}
