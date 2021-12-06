package com.yrt.project.api.business.warehouse.vo.grn;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreateBarcodeForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="条码类别 0-单一条码 1-多条码",required=true)
	@NotNull(message = "条码类别不能为空")
	private Integer barcode_type;
	
	@ApiModelProperty(value="产品数",required=true)
	@NotNull(message = "产品数不能为空")
	private Long barcode_number;

}
