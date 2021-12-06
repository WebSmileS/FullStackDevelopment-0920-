package com.yrt.project.api.business.contract.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ContractProductForm implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@NotNull(message="合同商品类别不能为空")
	@ApiModelProperty(value = "合同商品类别 0:医院经销商合同内经销商(自建)产品   1:厂商产品", required = true)
	private Integer product_type;
	
	@ApiModelProperty(value = "产品厂商内部编号", required = true)
	@NotNull(message="产品厂商内部编码不能为空")
	private Long product_vendor_inner_sn;
	
	@ApiModelProperty(value = "产品厂商名称", required = true)
	@NotBlank(message="产品厂商名称不能为空")
	private String product_vendor_name;
	
	@NotNull(message="商品内部编码不能为空")
	@ApiModelProperty(value = "商品内部编码", required = true)
	private Long product_inner_sn;
	
	@ApiModelProperty(value = "产品名称", required = true)
	@NotBlank(message="产品名称不能为空")
	private String product_name;

	@NotNull(message="商品规格型号内部编号不能为空")
	@ApiModelProperty(value = "商品规格型号内部编号", required = true)
	private Long specification_inner_sn;
	
	@ApiModelProperty(value = "商品规格型号", required = true)
	@NotBlank(message="商品规格型号不能为空")
	private String specification;
	
	@NotNull(message="商品包装单位内部编号不能为空")
	@ApiModelProperty(value = "商品包装单位内部编号", required = true)
	private Long unit_inner_sn;
	
	@ApiModelProperty(value = "商品包装单位", required = true)
	@NotBlank(message="商品包装单位不能为空")
	private String unit_name;
	
	@NotNull(message="商品价格不能为空")
	@ApiModelProperty(value = "商品价格", required = true)
	private BigDecimal price =new BigDecimal(0);
}
