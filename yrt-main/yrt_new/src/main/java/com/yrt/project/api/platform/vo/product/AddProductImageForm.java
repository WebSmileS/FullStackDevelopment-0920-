package com.yrt.project.api.platform.vo.product;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="前端新增产品相关图片",description="前端新增产品相关图片")
public class AddProductImageForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="厂商ID",required=true)
	@NotNull(message = "厂商ID不能为空")
	private Long vendor_inner_sn;
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message = "产品ID不能为空")
	private Long product_inner_sn;
	@ApiModelProperty(value="类别ID",required=true)
	@NotNull(message = "图片类别ID不能为空")
	private Integer type;
	@ApiModelProperty(value="开始日期",required=false)
	private Date begin_date;
	@ApiModelProperty(value="截止日期",required=false)
	private Date end_date;
	@ApiModelProperty(value="统一资源定位符",required=true)
	@NotBlank(message = "统一资源定位符不能为空")
	private String url;

}
