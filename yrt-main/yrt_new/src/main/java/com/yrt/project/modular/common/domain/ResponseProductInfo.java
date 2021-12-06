package com.yrt.project.modular.common.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ResponseProductInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "前端使用的唯一编码")
	private String id;
	@ApiModelProperty(value = "厂商内部编号 ")
	private Long vendor_inner_sn;
	@ApiModelProperty(value = "厂商名称")
	private String vendor_name;
	@ApiModelProperty(value = "产品厂商内部编号 ")
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value = "产品厂商名称")
	private String product_vendor_name;
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	@ApiModelProperty(value = "注册证号")
	private String mdrf_sn;
	@ApiModelProperty(value = "型号规格内部编号")
	private Long specification_inner_sn;
	@ApiModelProperty(value = "型号规格")
	private String specification;
	@ApiModelProperty(value = "产品厂商自定编号(编码)")
	private String specification_vendor_sn;
	@ApiModelProperty(value = "常用位内部编号")
	private Long common_use_unit_inner_sn;
	@ApiModelProperty(value = "常用单位名称")
	private String common_use_unit;
	@ApiModelProperty(value = "产品类型 0:经销商(自建)产品   1:厂商产品")
	private Integer product_type;
	@ApiModelProperty(value = "最小单位编号")
	private Long min_unit_inner_sn;
	@ApiModelProperty(value = "最小单位名称")
	private String min_unit;
	@ApiModelProperty(value = "仓库ID")
	private Long warehouse_inner_sn;
	@ApiModelProperty(value = "仓库名称")
	private String warehouse_name;
	@ApiModelProperty(value = "最小单位数量")
	private BigDecimal quantity;
	
}
