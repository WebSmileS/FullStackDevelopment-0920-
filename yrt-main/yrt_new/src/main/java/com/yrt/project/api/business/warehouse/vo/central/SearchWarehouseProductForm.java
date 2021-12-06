package com.yrt.project.api.business.warehouse.vo.central;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchWarehouseProductForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="机构ID",required=true)
	@NotNull(message="机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="仓库ID",required=false)
	private Long warehouse_inner_sn;
	
	@ApiModelProperty(value="产品名称",required=false)
	private String product_name;
	
	@ApiModelProperty(value="规格型号名称",required=false)
	private String specification;
	
	@ApiModelProperty(value="批号",required=false)
	private String batch_number;
}
