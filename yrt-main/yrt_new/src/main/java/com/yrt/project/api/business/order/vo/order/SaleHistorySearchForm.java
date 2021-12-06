package com.yrt.project.api.business.order.vo.order;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SaleHistorySearchForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="采购方名称",required=false)
	private String partA_name;
	
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message="当前机构ID不能为空")
	private Long partB_id;
	
	@ApiModelProperty(value="单据编号",required=false)
	private String order_sn;
	
	@ApiModelProperty(value="状态",required=false)
	private String status;
	
}
