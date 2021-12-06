package com.yrt.project.api.business.inquiry.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InquirySearchForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "询价机构ID", hidden = true)
	private Long partA;
	
	@ApiModelProperty(value = "被询价机构ID", hidden = true)
	private Long partB;

	@ApiModelProperty(value = "询价机构/询价单号", required = false)
	private String key;

	@ApiModelProperty(value = "状态(多个状态用','分割)", required = false)
	private String status;
}
