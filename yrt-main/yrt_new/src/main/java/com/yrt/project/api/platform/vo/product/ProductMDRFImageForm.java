package com.yrt.project.api.platform.vo.product;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="前端新增产品登记表图片",description="前端新增产品登记表图片")
public class ProductMDRFImageForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="统一资源定位符",required=true)
	@NotBlank(message = "统一资源定位符不能为空")
	private String url;
	@ApiModelProperty(value="文件名",required=true)
	@NotBlank(message = "文件名")
	private String file_name;

}
