package com.yrt.project.api.business.warehouse.vo.grn;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GrnIds implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "当前机构ID", required = true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;

	@ApiModelProperty(value="入库单ID",required=true)
	@NotNull(message = "入库单ID不能为空")
	private Long grn_inner_sn;

	@ApiModelProperty(value="关联的单据ID，非必传")
	private Long voucher_inner_sn;
	
	@ApiModelProperty(value = "仓库ID", required = true)
	@NotNull(message = "仓库ID不能为空")
	private Long warehouse_inner_sn;
	
	@ApiModelProperty(value = "仓库名称", required = true)
	@NotBlank(message = "仓库名称不能为空")
	private String warehouse_name;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
}