package com.yrt.project.api.business.warehouse.vo.grn;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GrnIDForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 当前机构ID */
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="入库单ID",required=true)
	@NotNull(message = "入库单ID不能为空")
	private Long grn_inner_sn;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
	
	public GrnIDForm() {
		
	}
	
	public GrnIDForm(Long org_id, Long grn_inner_sn, BillsType billsType) {
		this.org_id = org_id;
		this.grn_inner_sn = grn_inner_sn;
		this.billsType = billsType;
	}
}
