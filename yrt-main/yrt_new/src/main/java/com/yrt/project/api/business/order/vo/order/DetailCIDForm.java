package com.yrt.project.api.business.order.vo.order;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DetailCIDForm implements Serializable{
	
 
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="采购方ID",required=true)
	@NotNull(message = "采购方ID不能为空")
	private Long partA_id;
	
	@ApiModelProperty(value="销售方ID",required=true)
	@NotNull(message = "销售方ID不能为空")
	private Long partB_id;
	
	@ApiModelProperty(value="订单ID ",required=true)
	@NotNull(message = "订单ID不能为空")
	private Long po_so_inner_sn;
	
	@ApiModelProperty(value="订单计划明细ID",required=true)
	@NotNull(message = "订单明细ID不能为空")
	private Long detail_inner_sn;
	
	@ApiModelProperty(value="产品类型 0:经销商(自建)产品   1:厂商产品",required=true)
	@NotNull(message = "产品类型不能为空")
	private Integer product_type;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;

}
