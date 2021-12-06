package com.yrt.project.api.business.inquiry.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InquiryDetailForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 明细内部编号 */
	private Long detail_inner_sn;
	/** (产品)厂商内部编号 */
	@ApiModelProperty(value="(产品)厂商ID",required=true)
	@NotNull(message = "产品)厂商ID不能为空")
	private Long vendor_inner_sn;
	/** 产品内部编号 */
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message = "产品ID不能为空")
	private Long product_inner_sn;
	/** 型号规格内部编号 */
	@ApiModelProperty(value="型号规格ID",required=true)
	@NotNull(message = "型号规格ID不能为空")
	private Long specification_inner_sn;
	/** 单位内部编号 */
	@ApiModelProperty(value="单位ID",required=false)
	private Long unit_inner_sn;
	/** 数量 */
	@ApiModelProperty(value="数量",required=false)
	private BigDecimal quantity;
	/** 单价 */
	@ApiModelProperty(value="单价",required=false)
	private BigDecimal unit_price;
	/** 折扣 */
	@ApiModelProperty(value="折扣",required=false)
	private BigDecimal discount;
	/** 税率 */
	@ApiModelProperty(value="税率",required=false)
	private BigDecimal rate;
	/** 排序号 */
	@ApiModelProperty(value="排序号",required=false)
	private Long sort_number;

}
