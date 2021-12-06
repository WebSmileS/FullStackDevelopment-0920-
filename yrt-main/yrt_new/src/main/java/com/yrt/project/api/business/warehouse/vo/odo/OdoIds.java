package com.yrt.project.api.business.warehouse.vo.odo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OdoIds implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="出库单ID",required=true)
	@NotNull(message = "出库单ID不能为空")
	private Long odo_inner_sn;
	
	@ApiModelProperty(value="仓库ID",required=true)
	@NotNull(message = "仓库ID不能为空")
	private Long warehouse_inner_sn;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
}