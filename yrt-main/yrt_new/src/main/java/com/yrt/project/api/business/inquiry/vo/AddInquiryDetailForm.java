package com.yrt.project.api.business.inquiry.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddInquiryDetailForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/** (产品)厂商内部编号 */
	@ApiModelProperty(value="产品厂商ID",required=true)
	@NotNull(message = "产品厂商ID不能为空")
	private Long vendor_inner_sn;
	@ApiModelProperty(value="产品厂商名称",required=true)
	@NotNull(message = "产品厂商名称不能为空")
	private String vendor_name;
	/** 产品内部编号 */
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message = "状态不能为空")
	private Long product_inner_sn;
	@ApiModelProperty(value="产品名称",required=true)
	@NotBlank(message = "产品名称不能为空")
	private String product_name;
	/** 型号规格内部编号 */
	@ApiModelProperty(value="型号规格",required=true)
	@NotNull(message = "型号规格不能为空")
	private Long specification_inner_sn;
	@ApiModelProperty(value="型号规格名称",required=true)
	@NotBlank(message = "型号规格名称不能为空")
	private String specification;
	/** 单位内部编号 */
	@ApiModelProperty(value="单位",required=true)
	@NotNull(message = "单位不能为空")
	private Long unit_inner_sn;
	@ApiModelProperty(value="单位名称",required=true)
	@NotBlank(message = "单位名称不能为空")
	private String unit;
	/** 数量 */
	@ApiModelProperty(value="数量",required=true)
	@NotNull(message = "数量不能为空")
	private BigDecimal quantity;
	/** 单价 */
	@ApiModelProperty(value="单价",required=false)
	private BigDecimal unit_price = new BigDecimal(0);
	/** 折扣 */
	@ApiModelProperty(value="折扣",required=false)
	private BigDecimal discount = new BigDecimal(1);
	/** 税率 */
	@ApiModelProperty(value="税率",required=false)
	private BigDecimal rate = new BigDecimal(0);
	/** 排序号 */
	@ApiModelProperty(value="排序号",required=false)
	private Long sort_number = 0L;

}
