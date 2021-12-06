package com.yrt.project.api.business.order.vo.record;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchSaleRecordForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="销售方机构ID",required=true)
	@NotNull(message = "销售方机构ID不能为空")
	private Long partB;
	
	@ApiModelProperty(value="采购方机构ID",required=true)
	@NotNull(message = "采购方机构ID不能为空")
	private Long partA;
	
	@ApiModelProperty(value="销售订单ID",required=true)
	@NotNull(message = "销售订单不能为空")
	private Long po_so_inner_sn;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
}
