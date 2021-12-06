package com.yrt.project.api.business.contract.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AnalyseDetailForm implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/** 合同商品类型 */
	@NotNull(message="合同商品类别不能为空")
	@ApiModelProperty(value = "合同商品类别 0:医院经销商合同内经销商(自建)产品   1:厂商产品", required = true)
	private Integer product_type;
	
	/** 厂家内部编号 */
	@ApiModelProperty(value = "厂商内部编号", required = false)
	@NotNull(message="厂商内部编号不能为空")
	private Long vendor_inner_sn;
	
	/** 商品内部编号 */
	@NotNull(message="商品内部编码不能为空")
	@ApiModelProperty(value = "商品内部编码", required = true)
	private Long product_inner_sn;

	/** 商品规格型号内部编号 */
	@NotNull(message="商品规格型号内部编号不能为空")
	@ApiModelProperty(value = "商品规格型号内部编号", required = true)
	private Long specification_inner_sn;
	
}
