package com.yrt.project.modular.product.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DealerProductUnit implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "经销商内部编号")
	private Long dealer_inner_sn;
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	@ApiModelProperty(value = "型号规格内部编号")
	private Long specification_inner_sn;
	@ApiModelProperty(value = "单位内部编号")
	private Long unit_inner_sn;
	@ApiModelProperty(value = "上级单位内部编号")
	private Long parent_unit_inner_sn;
	@ApiModelProperty(value = "单位名称")
	private String name;
	@ApiModelProperty(value = "最小计量/倍数")
	private BigDecimal measure;
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	@ApiModelProperty(value = "规格型号")
	private String specification;
	@ApiModelProperty(value = "是否常用单位")
	private Integer is_common_use;
	
	private List<DealerProductUnit> children;
	
	private DealerProductUnit parent;
}
