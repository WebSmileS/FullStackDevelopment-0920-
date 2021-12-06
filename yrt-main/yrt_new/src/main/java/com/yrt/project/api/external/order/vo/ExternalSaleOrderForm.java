package com.yrt.project.api.external.order.vo;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ExternalSaleOrderForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="相关机构ID",required=true)
	@NotNull(message = "相关机构ID不能为空")
	private Long organization_inner_sn;
	
	@ApiModelProperty(value="相关机构类型",required=true)
	private Integer org_type;
	
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description = "";
	
	@ApiModelProperty(value="订单明细列表",required=true)
	@NotNull(message = "订单明细列表不能为空")
	private List<ExternalSaleOrderDetailForm> details;
}
