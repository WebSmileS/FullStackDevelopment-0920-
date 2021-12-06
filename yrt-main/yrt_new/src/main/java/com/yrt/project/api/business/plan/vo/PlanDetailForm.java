package com.yrt.project.api.business.plan.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PlanDetailForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** (产品)厂商内部编号 */
	@ApiModelProperty(value="(产品)厂商ID",required=true)
	@NotNull(message = "产品厂商ID不能为空")
	private Long vendor_inner_sn;
	@ApiModelProperty(value="产品厂商名称",required=true)
	@NotBlank(message = "产品厂商名称不能为空")
	private String vendor_name;
	
	/** 产品内部编号 */
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message = "产品ID不能为空")
	private Long product_inner_sn;
	@ApiModelProperty(value="产品名称名称",required=true)
	@NotBlank(message = "产品名称不能为空")
	private String product_name;
	
	/** 型号规格内部编号 */
	@ApiModelProperty(value="型号规格ID",required=true)
	@NotNull(message = "型号规格ID不能为空")
	private Long specification_inner_sn;
	@ApiModelProperty(value="型号规格名称",required=true)
	@NotBlank(message = "型号规格名称不能为空")
	private String specification;
	
	/** 单位内部编号 */
	@ApiModelProperty(value="单位ID",required=true)
	@NotNull(message = "单位ID不能为空")
	private Long unit_inner_sn;
	@ApiModelProperty(value="单位名称",required=true)
	@NotBlank(message = "单位名称不能为空")
	private String unit_name;
	
	/** 数量 */
	@ApiModelProperty(value="数量",required=false)
	@Digits(integer=8, fraction=4, message="倍数只能是8位整数和最大4位小数的数字")
	private BigDecimal quantity = new BigDecimal(0);
	
	/** 排序号 */
	@ApiModelProperty(value="排序号",required=false)
	private Integer sort_number = 0;
	@ApiModelProperty(value="批号",required=false)
	private String batch_number = "";
	
	@ApiModelProperty(value="产品类型 0:经销商(自建)产品   1:厂商产品",required=true)
	@NotNull(message = "产品类型不能为空")
	private Integer product_type;
}
