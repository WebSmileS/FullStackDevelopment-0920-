package com.yrt.project.api.business.warehouse.vo;
import javax.validation.constraints.NotBlank;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ScanBarcodeForm {

	@ApiModelProperty(value="标签/UDI条码",required=true)
	@NotBlank(message = "标签/UDI条码不能为空")
	private String barcode;
	
	@ApiModelProperty(value = "相关机构ID")
	private Long organization_inner_sn;
	
	@ApiModelProperty(value = "仓库ID")
	private Long warehouse_inner_sn;
	
	@ApiModelProperty(value="业务类型")
	private BillsType bills_type;
}