package com.yrt.project.api.business.finance.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchInventoryForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "当前机构ID", required = true)
	@NotNull(message = "当前机构ID不能空")
	private Long org_id;
	
	@ApiModelProperty(value = "仓库ID")
	private Long warehouse_model_inner_sn;
	@ApiModelProperty(value = "仓库人员ID")
	private Long employee_inner_sn;
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	/** 排序列 */
	private String orderByColumn;

}
