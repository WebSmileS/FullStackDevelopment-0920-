package com.yrt.project.modular.contract.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ContractVendorProduct implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "甲方内部编号")
	private Long partA;
	@ApiModelProperty(value = "乙方内部编号")
	private Long partB;
	@ApiModelProperty(value = "乙方名称")
	private String partB_name;
	@ApiModelProperty(value = "合同内部编号")
	private Long contract_inner_sn;
	/** 厂商内部编号 如果厂商作为乙方是可以为NULL*/
	@ApiModelProperty(value = "厂商内部编号 如果厂商作为乙方是可以为NULL")
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value = "厂商名称")
	private String product_vendor_name;
	/** 产品内部编号 */
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	/** 型号规格内部编号 */
	@ApiModelProperty(value = "型号规格内部编号")
	private Long specification_inner_sn;
	@ApiModelProperty(value = "型号规格名称")
	private String specification_name;
	@ApiModelProperty(value = "录入人员内部编号")
	private Long inputer_inner_sn;
	@ApiModelProperty(value = "录入人员名字")
	private String inputer_name;
	/** 单位内部编号 */
	@ApiModelProperty(value = "单位内部编号")
	private Long unit_inner_sn;
	@ApiModelProperty(value = "单位名称")
	private String unit_name;
	@ApiModelProperty(value = "价格")
	private BigDecimal price = new BigDecimal(0);
	@ApiModelProperty(value = "产品类型 1-厂商产品 0-经销商自建产品")
	private Integer product_type;
	@ApiModelProperty(value = "是否存在注册证图片")
	private Boolean hasCertImage;
}
