package com.yrt.project.api.business.applyvoucher.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DetailIdsForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="单据明细ID",required=false)
	@NotNull(message = "单据明细ID不能为空")
	private Long detail_id;
	
	/** 产品类型 0:经销商(自建)产品   1:厂商产品 */
	@ApiModelProperty(value="产品类型 0:经销商(自建)产品   1:厂商产品",required=true)
	@NotNull(message = "产品类型不能为空")
	private Integer product_type;
}
