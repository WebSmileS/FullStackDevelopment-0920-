package com.yrt.project.modular.product.domain;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DealerProductSpecification implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "经销商内部编号")
	private Long dealer_inner_sn;
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	@ApiModelProperty(value = "型号规格内部编号")
	private Long specification_inner_sn;
	@ApiModelProperty(value = "状态 0-禁用  1-正常")
	private Integer status;
	@ApiModelProperty(value = "已删除 0-可用 1-删除")
	private Integer is_delete;
	@ApiModelProperty(value = "型号规格")
	private String specification;
	@ApiModelProperty(value = "常用单位内部编号")
	private Long common_use_unit_inner_sn;
	@ApiModelProperty(value = "常用单位")
	private String common_use_unit;

}
