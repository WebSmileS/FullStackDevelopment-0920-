package com.yrt.project.api.business.dealer.vo.dealerproduct;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UnitIDForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="产品厂商ID",required=true)
	@NotNull(message="产品厂商ID")
	private Long org_id;
	/** 产品内部编号 */
	@ApiModelProperty(value="产品编号",required=true)
	@NotNull(message="产品编号不能为空")
	private Long product_inner_sn;
	/** 型号规格内部编号 */
	@ApiModelProperty(value="型号规格编号",required=true)
	@NotNull(message="型号规格编号不能为空")
	private Long specification_inner_sn;
	/** 型号规格内部编号 */
	@ApiModelProperty(value="包装单位编号",required=true)
	@NotNull(message="包装单位编号不能为空")
	private Long unit_inner_sn;
	@ApiModelProperty(value="是否常用 0-不常用 1-常用",required=true)
	@NotNull(message = "是否常用不能为空")
	private Integer is_common_use;
}
