package com.yrt.project.api.business.order.vo.record;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SaleRecordIDForm implements Serializable{
	
 
	private static final long serialVersionUID = 1L;
	
	/** 采购方ID */
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	/** 订单ID */
	@ApiModelProperty(value="销售单ID ",required=true)
	@NotNull(message = "销售单ID不能为空")
	private Long sl_inner_sn;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;

}
