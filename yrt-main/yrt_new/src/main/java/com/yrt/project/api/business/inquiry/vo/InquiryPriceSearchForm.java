package com.yrt.project.api.business.inquiry.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InquiryPriceSearchForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "当前机构ID", required = true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value = "是否最新报价,否则最低报价(默认true)", required = false)
	private Boolean isNew = true;
	
	@ApiModelProperty(value="产品厂商ID",required=false)
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value="产品名称",required=true)
	@NotBlank(message = "产品名称不能为空")
	private String product_name;
	@ApiModelProperty(value="型号规格名称",required=false)
	private String specification = "";
}
