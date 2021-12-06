package com.yrt.project.api.business.tag.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchUDIRefProductForm {

	@ApiModelProperty(value = "产品名称", required = false)
	private String name;

	@ApiModelProperty(value = "厂商ID", required = false)
	private Long vendor_inner_sn;
	
	@ApiModelProperty(value = "自定义类型编号", required = false)
	private Long type_inner_sn;

	@ApiModelProperty(value = "68分类编号", required = false)
	private Long code68_sn;

	@ApiModelProperty(value = "型号规格厂商自定编号", required = false)
	private String specification_vendor_sn;

	@ApiModelProperty(value = "型号规格", required = false)
	private String specification;
}
