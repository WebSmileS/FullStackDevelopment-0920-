package com.yrt.project.api.business.warehouse.vo.grn;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ScanBarcodeForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="条码编码",required=true)
	@NotBlank(message = "条码编码不能为空")
	private String barcode;
	
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
}
