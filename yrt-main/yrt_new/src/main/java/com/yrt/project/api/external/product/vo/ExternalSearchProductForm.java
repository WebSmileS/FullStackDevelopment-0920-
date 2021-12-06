package com.yrt.project.api.external.product.vo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ExternalSearchProductForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="合同相关机构ID，查合同内产品时必填")
	private Long organization_inner_sn;
	
	@ApiModelProperty(value="产品名称",required=false)
	private String product_name;
	
	@ApiModelProperty(value="厂商名称",required=false)
	private String vendor_name;
	
	@ApiModelProperty(value="规格型号",required=false)
	private String specification;
	
	@ApiModelProperty(value = "发布截止日期>=")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	
	@ApiModelProperty(value="仅查询合同内产品",required=false)
	private Boolean onlyContrctProduct = false;
}
