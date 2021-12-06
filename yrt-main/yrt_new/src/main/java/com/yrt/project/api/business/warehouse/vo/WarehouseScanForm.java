package com.yrt.project.api.business.warehouse.vo;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WarehouseScanForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="仓库ID(用于检查条码的储位是否中这个仓库中)",required=false)
	private Long warehouse_model_inner_sn;
	
	@ApiModelProperty(value="条码",required=true)
	@NotBlank(message = "条码不能为空")
	private String barcode;
}
