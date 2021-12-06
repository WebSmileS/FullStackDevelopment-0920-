package com.yrt.project.api.business.warehouse.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchUnPlaceProductForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message="当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="产品名称/产品编码/厂商名称",required=false)
	private String key;
	
	@ApiModelProperty(value="仓库ID",required=true)
	@NotNull(message="必须选择一个仓库")
	private Long warehouse_inner_sn;
	
	@ApiModelProperty(value="产品条码",required=false)
	private String barcode;
}
