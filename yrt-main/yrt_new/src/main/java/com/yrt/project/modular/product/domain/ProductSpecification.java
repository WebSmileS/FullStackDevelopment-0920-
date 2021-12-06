package com.yrt.project.modular.product.domain;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductSpecification implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="厂商内部编号")
	private Long vendor_inner_sn;
	@ApiModelProperty(value="产品内部编号")
	private Long product_inner_sn;
	@ApiModelProperty(value="型号规格内部编号")
	private Long specification_inner_sn;
	@ApiModelProperty(value="状态 0-禁用  1-正常")
	private Integer status;
	@ApiModelProperty(value="已删除 0-可用 1-删除")
	private Integer is_delete;
	@ApiModelProperty(value="型号规格")
	private String specification;
	@ApiModelProperty(value="产品厂商自定编号(编码)")
	private String specification_vendor_sn;
	@ApiModelProperty(value="常用单位内部编号")
	private Long common_use_unit_inner_sn;
	@ApiModelProperty(value="常用单位")
	private String common_use_unit;
	
	@ApiModelProperty(value = "医保编号")
	private String default_health_care_sn;
	@ApiModelProperty(value = "阳光流水号")
	private String default_sun_sn;

}
