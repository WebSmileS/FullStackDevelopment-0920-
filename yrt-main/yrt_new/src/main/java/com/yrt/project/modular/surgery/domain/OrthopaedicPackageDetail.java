package com.yrt.project.modular.surgery.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrthopaedicPackageDetail implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "手术包内部编号")
	private Long ois_inner_sn;
	@ApiModelProperty(value = "明细内部编号")
	private Long detail_inner_sn;
	
	@ApiModelProperty(value = "厂商内部编号 ")
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value = "厂商名称")
	private String product_vendor_name;
	
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	@ApiModelProperty(value = "型号规格内部编号")
	private Long specification_inner_sn;
	@ApiModelProperty(value = "型号规格")
	private String specification;
	@ApiModelProperty(value = "厂商产品编号")
	private String product_vendor_sn;
	@ApiModelProperty(value = "产品厂商自定编号(编码)")
	private String specification_vendor_sn;
	@ApiModelProperty(value = "产品包装单位内部编号")
	private Long unit_inner_sn;
	@ApiModelProperty(value = "产品单位名称")
	private String unit_name;
	@ApiModelProperty(value = "数量")
	private BigDecimal quantity;
	@ApiModelProperty(value = "排序号")
	private Integer sort_number;
	@ApiModelProperty(value = "类型")
	private Integer type;
}
