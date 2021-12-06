package com.yrt.project.api.platform.vo.product;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddMDRFImageForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="厂商ID",required=true)
	@NotNull(message = "厂商ID不能为空")
	private Long vid;
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message = "产品ID不能为空")
	private Long pid;
	@ApiModelProperty(value="注册证ID",required=true)
	@NotNull(message = "注册证ID不能为空")
	private Long mid;
	@ApiModelProperty(value="文件路径",required=true)
	@NotBlank(message = "文件不能为空")
	private String url;
	@ApiModelProperty(value="文件名",required=true)
	@NotBlank(message = "文件名不能为空")
	private String fileName;
}
