package com.yrt.project.api.platform.surgery.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrthopaedicDetailForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 明细内部编号 */
	@ApiModelProperty(value="明细ID",required=false)
	private Long detail_inner_sn;
	/** (产品)厂商内部编号 */
	@ApiModelProperty(value="(产品)厂商ID",required=true)
	@NotNull(message = "(产品)厂商ID不能为空")
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value="厂商名称",required=true)
	@NotBlank(message = "厂商名称不能为空")
	private String product_vendor_name;
	/** 产品内部编号 */
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message = "产品ID不能为空")
	private Long product_inner_sn;
	@ApiModelProperty(value="产品名称",required=true)
	@NotBlank(message = "产品名称不能为空")
	private String product_name;
	/** 型号规格内部编号 */
	@ApiModelProperty(value="型号规格ID",required=true)
	@NotNull(message = "型号规格ID不能为空")
	private Long specification_inner_sn;
	@ApiModelProperty(value="型号规格",required=true)
	@NotBlank(message = "型号规格不能为空")
	private String specification;
	/** 单位内部编号 */
	@ApiModelProperty(value="单位ID",required=true)
	@NotNull(message = "单位ID不能为空")
	private Long unit_inner_sn;
	@ApiModelProperty(value="单位名称",required=true)
	@NotBlank(message = "单位名称不能为空")
	private String unit_name;
	@ApiModelProperty(value="排序号",required=true)
	@NotNull(message = "排序号不能为空")
	private Integer sort_number;
	/** 数量 */
	@ApiModelProperty(value="数量",required=false)
	private BigDecimal quantity = new BigDecimal(0);
	
}
