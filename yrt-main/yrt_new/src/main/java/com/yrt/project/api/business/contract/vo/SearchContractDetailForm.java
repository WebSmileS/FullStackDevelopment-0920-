package com.yrt.project.api.business.contract.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchContractDetailForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "产品名称", required = false)
	private String product_name;

	@ApiModelProperty(value = "厂商名称", required = false)
	private String product_vendor_name;

	@ApiModelProperty(value = "型号规格", required = false)
	private String specification;
}
