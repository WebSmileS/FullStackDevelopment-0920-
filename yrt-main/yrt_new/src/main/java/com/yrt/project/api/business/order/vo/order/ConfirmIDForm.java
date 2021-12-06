package com.yrt.project.api.business.order.vo.order;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ConfirmIDForm implements Serializable{
	
 
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="采购方ID",required=true)
	private Long partA_id;
	
	@ApiModelProperty(value="销售方ID",required=true)
	private Long partB_id;
	
	@ApiModelProperty(value="订单ID ",required=true)
	private Long po_so_inner_sn;
	
	@ApiModelProperty(value="单据编号",required=false)
	private String so_sn;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;

}
