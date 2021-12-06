package com.yrt.project.api.external.order.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ExternalSaleOrderDetailForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="厂商ID",required=true)
	@NotNull(message = "厂商ID不能为空")
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value="产品厂商名称",required=true)
	@NotBlank(message = "产品厂商名称不能为空")
	private String product_vendor_name;
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message = "产品ID不能为空")
	private Long product_inner_sn;
	@ApiModelProperty(value="产品名称",required=true)
	@NotBlank(message = "产品名称不能为空")
	private String product_name;
	@ApiModelProperty(value="型号规格ID",required=true)
	@NotNull(message = "型号规格ID不能为空")
	private Long specification_inner_sn;
	@ApiModelProperty(value="型号规格名称",required=true)
	@NotBlank(message = "型号规格名称不能为空")
	private String specification;
	@ApiModelProperty(value="单位ID",required=true)
	@NotNull(message = "单位ID不能为空")
	private Long unit_inner_sn;
	@ApiModelProperty(value="包装单位名称",required=true)
	@NotBlank(message = "包装单位名称不能为空")
	private String unit_name;
	@ApiModelProperty(value="数量",required=true)
	@NotNull(message = "数量不能为空")
	private BigDecimal quantity;
	@ApiModelProperty(value="不含税单价",required=true)
	@NotNull(message = "不含税单价不能为空")
	private BigDecimal unit_price;
	@ApiModelProperty(value="折扣",hidden=true)
	private BigDecimal discount = new BigDecimal(1);
	/** 税率 */
	@ApiModelProperty(value="税率",required=true)
	@NotNull(message = "税率不能为空")
	private BigDecimal rate;
//	@ApiModelProperty(value="排序号",required=false)
//	private Integer sort_number = 0;
	
	@ApiModelProperty(value="批号",required=false)
	private String batch_number = "";
}
