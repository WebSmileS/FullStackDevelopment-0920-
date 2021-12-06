package com.yrt.project.api.business.order.vo.order;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SaleDetailSearchForm implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="采购方机构ID",required=true)
	@NotNull(message = "采购方机构ID不能为空")
	private Long partA_id;
	
	@ApiModelProperty(value="订单ID",required=true)
	@NotNull(message = "订单ID不能为空")
	private Long po_so_inner_sn;
	
	@ApiModelProperty(value="仓库ID",required=true)
	@NotEmpty(message = "仓库ID不能为空")
	private List<WarehouseIDs> warehouse_ids;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
	
	
}
