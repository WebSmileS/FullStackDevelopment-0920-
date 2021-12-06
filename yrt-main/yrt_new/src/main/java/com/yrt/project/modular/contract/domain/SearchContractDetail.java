package com.yrt.project.modular.contract.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchContractDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "甲方内部编号")
	private Long partA;
	@ApiModelProperty(value = "甲方名称")
	private String partA_name;
	
	@ApiModelProperty(value = "乙方内部编号")
	private Long partB;
	@ApiModelProperty(value = "乙方名称")
	private String partB_name;
	
	@ApiModelProperty(value = "合同编号")
	private String contract_sn;
	
	@ApiModelProperty(value = "合同名称")
	private String contract_name;
	
	@ApiModelProperty(value = "产品厂商名称")
	private String product_vendor_name;
	
	@ApiModelProperty(value = "产品名称")
	private String product_name;

	@ApiModelProperty(value = "产品规格型号")
	private String specification;
	
	@ApiModelProperty(value = "产品包装单位")
	private String unit;
	
	@ApiModelProperty(value = "产品价格")
	private BigDecimal price =new BigDecimal(0);
	
	@ApiModelProperty(value="产品类型 0:经销商(自建)产品   1:厂商产品")
	private Integer product_type;
	
	@ApiModelProperty(value = "合同类型  0 hospitalDealer 1 hospitalVendor 2 dealerVendor 3 dealerDealer")
	private Integer contract_type;
	
	@ApiModelProperty(value = "合同状态")
	private Integer status;
}
