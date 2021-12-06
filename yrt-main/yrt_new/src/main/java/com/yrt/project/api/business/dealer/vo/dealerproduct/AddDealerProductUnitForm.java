package com.yrt.project.api.business.dealer.vo.dealerproduct;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddDealerProductUnitForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 经销商内部编号 */
	@ApiModelProperty(value="经销商编号",required=true)
	@NotNull(message="经销商编号不能为空")
	private Long dealer_inner_sn;
	/** 产品内部编号 */
	@ApiModelProperty(value="产品编号",required=true)
	@NotNull(message="产品编号不能为空")
	private Long product_inner_sn;
	/** 型号规格内部编号 */
	@ApiModelProperty(value="型号规格编号",required=true)
	@NotNull(message="型号规格编号不能为空")
	private Long specification_inner_sn;
	/** 上级(小)单位内部编号 */
	@ApiModelProperty(value="上级(小)单位内部编号",required=false)
	private Long parent_unit_inner_sn;
	/** 名称* */
	@ApiModelProperty(value="名称",required=true)
	@NotBlank(message = "名称不能为空")
	private String name;
	/** 最小计量/倍数 */
	@ApiModelProperty(value="最小计量/倍数",required=true)
	@Digits(integer=8, fraction=4, message="倍数只能是8位整数和最大4位小数的数字")
	private BigDecimal measure;
	@ApiModelProperty(value="是否常用 0-不常用 1-常用",required=true)
	@NotNull(message = "是否常用不能为空")
	private Integer is_common_use;
}
