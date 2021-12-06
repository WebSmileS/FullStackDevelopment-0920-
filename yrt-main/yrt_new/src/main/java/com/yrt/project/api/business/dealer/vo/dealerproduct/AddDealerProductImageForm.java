package com.yrt.project.api.business.dealer.vo.dealerproduct;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="前端新增产品相关图片",description="前端新增产品相关图片")
public class AddDealerProductImageForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="经销商ID",required=true)
	@NotNull
	private Long dealer_inner_sn;
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull
	private Long product_inner_sn;
	@ApiModelProperty(value="类别ID",required=true)
	@NotNull
	private Long type;
	@ApiModelProperty(value="开始日期",required=false)
	private Date begin_date;
	@ApiModelProperty(value="截止日期",required=false)
	private Date end_date;
	@ApiModelProperty(value="统一资源定位符",required=true)
	@NotBlank
	private String url;

}
