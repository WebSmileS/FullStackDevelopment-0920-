package com.yrt.project.api.business.warehouse.vo.odo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OdoIDForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 当前机构ID */
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="出库单ID",required=true)
	@NotNull(message = "出库单ID不能为空")
	private Long odo_inner_sn;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
	
	public OdoIDForm() {
		
	}
	
	public OdoIDForm(Long org_id, Long odo_inner_sn, BillsType billsType) {
		this.org_id = org_id;
		this.odo_inner_sn = odo_inner_sn;
		this.billsType = billsType;
	}
}
