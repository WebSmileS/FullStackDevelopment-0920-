package com.yrt.project.api.business.warehouse.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WarehouseSearchProductForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message="当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="产品名称",required=false)
	private String product_name;
	
	@ApiModelProperty(value="仓库ID",required=false)
	private Long warehouse_inner_sn;
	
	@ApiModelProperty(value="规格型号名称",required=false)
	private String specification;
	
	@ApiModelProperty(value="批号",required=false)
	private String batch_number;
	
	@ApiModelProperty(value="手机搜索关键字",required=false)
	private String key;
	
	@ApiModelProperty(value="厂商ID",required=false)
	private Long product_vendor_inner_sn;
	
	@ApiModelProperty(value="经销商ID",required=false)
	private Long product_dealer_inner_sn;
	
	@ApiModelProperty(value="产品ID",required=false)
	private Long product_inner_sn;
	
	@ApiModelProperty(value="产品规格ID",required=false)
	private Long specification_inner_sn;

	@ApiModelProperty(value="排序字段")
	private String orderByColumn;
}
