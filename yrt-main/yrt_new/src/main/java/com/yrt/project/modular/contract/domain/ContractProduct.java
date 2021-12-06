package com.yrt.project.modular.contract.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ContractProduct  implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="产品类型 0:经销商(自建)产品   1:厂商产品")
	private Integer product_type;
	
	@ApiModelProperty(value = "厂商内部编号")
	private Long product_vendor_inner_sn;
	
	@ApiModelProperty(value = "厂商名称")
	private String product_vendor_name;
	
	@ApiModelProperty(value = "商品内部编码")
	private Long product_inner_sn;
	
	@ApiModelProperty(value = "产品名称")
	private String product_name;

	@ApiModelProperty(value = "商品规格型号内部编号")
	private Long specification_inner_sn;
	
	@ApiModelProperty(value = "商品规格型号")
	private String specification;
	
	@ApiModelProperty(value = "商品包装单位内部编号")
	private Long unit_inner_sn;
	
	@ApiModelProperty(value = "商品包装单位")
	private String unit;
	
	@ApiModelProperty(value = "商品价格")
	private BigDecimal price =new BigDecimal(0);
}
