package com.yrt.project.api.business.plan.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchBatchNumForm implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message="当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="产品厂商ID",required=true)
	@NotNull(message="产品厂商ID不能为空")
	private Long product_vendor_inner_sn;
	
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message="产品ID不能为空")
	private Long product_inner_sn;
	
	@ApiModelProperty(value="型号规格ID",required=true)
	@NotNull(message="型号规格ID不能为空")
	private Long specification_inner_sn;
	
	@ApiModelProperty(value="产品类型 0:经销商(自建)产品   1:厂商产品",required=true)
	@NotNull(message="产品类型ID不能为空")
	private Integer product_type;
}
