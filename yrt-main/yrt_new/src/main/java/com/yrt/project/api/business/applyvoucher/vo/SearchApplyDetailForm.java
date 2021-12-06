package com.yrt.project.api.business.applyvoucher.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchApplyDetailForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="医院ID",required=true)
	@NotNull(message = "医院不能为空")
	private Long hospital_inner_sn;
	
	@ApiModelProperty(value="单据ID",required=true)
	@NotNull(message = "单据ID不能为空")
	private Long order_id;
	
	@ApiModelProperty(value="仅显示待发放明细,默认true",required=false)
	private Boolean onlyUnGrant = true;
	
	@ApiModelProperty(value="库存来源,默认-1所有仓库",required=false)
	private Long warehouse_inner_sn = -1L;
}
