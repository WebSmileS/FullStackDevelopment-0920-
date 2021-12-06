package com.yrt.project.api.business.order.vo.record;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchSaleRecordDetailForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="销售单ID",required=true)
	@NotNull(message = "销售单不能为空")
	private Long sl_inner_sn;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
	
//	@ApiModelProperty(value="是否显示全部明细，否则只显示0-待生成出库单 (默认:true)",required=false)
//	private Boolean isAll = true;
}
