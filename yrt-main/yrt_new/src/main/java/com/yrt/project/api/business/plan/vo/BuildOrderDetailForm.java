package com.yrt.project.api.business.plan.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BuildOrderDetailForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 厂商内部编号如果是经销商自建产品则是经销商ID */
	@ApiModelProperty(value="厂商内部编号如果是经销商自建产品则是经销商ID",required=true)
	@NotNull(message = "产品厂商ID不能为空")
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value="产品厂商名称",required=true)
	@NotBlank(message = "产品厂商名称不能为空")
	private String product_vendor_name;
	/** 产品内部编号 */
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message = "产品ID不能为空")
	private Long product_inner_sn;
	@ApiModelProperty(value="产品名称",required=true)
	@NotBlank(message = "产品名称不能为空")
	private String product_name;
	/** 型号规格内部编号 */
	@ApiModelProperty(value="型号规格ID",required=true)
	@NotNull(message = "型号规格ID不能为空")
	private Long specification_inner_sn;
	@ApiModelProperty(value="型号规格名称",required=true)
	@NotBlank(message = "型号规格名称不能为空")
	private String specification;
	/** 单位内部编号 */
	@ApiModelProperty(value="单位ID",required=true)
	@NotNull(message = "单位ID不能为空")
	private Long unit_inner_sn;
	@ApiModelProperty(value="包装单位名称",required=true)
	@NotBlank(message = "包装单位名称不能为空")
	private String unit_name;
	/** 数量 */
	@ApiModelProperty(value="数量",required=false)
	private BigDecimal quantity = new BigDecimal(0);
	/** 单价 */
	@ApiModelProperty(value="单价",required=false)
	private BigDecimal unit_price = new BigDecimal(0);
	/** 折扣 */
	@ApiModelProperty(hidden = true)
	private BigDecimal discount = new BigDecimal(1);
	/** 税率 */
	@ApiModelProperty(hidden = true)
	private BigDecimal rate = new BigDecimal(0);
	
	@ApiModelProperty(value="供应商ID",required=true)
	@NotNull(message = "供应商ID不能为空")
	private Long supplierID;
	@ApiModelProperty(value="供应商类型  0-经销商 1-厂商",required=true)
	@NotNull(message = "供应商类型不能为空")
	private Integer supplierType;
	@ApiModelProperty(value="自定义编码 供应商ID+供应商类型组成，例如：供应商是经销商15，customID=150;供应商是厂商15，customID=151",required=true)
	@NotBlank(message = "自定义编码不能为空")
	private String customID;
	@ApiModelProperty(value="产品类型 0:经销商(自建)产品   1:厂商产品",required=true)
	@NotNull(message = "产品类型不能为空")
	/** 产品类型 0:经销商(自建)产品   1:厂商产品 */
	private Integer product_type;
	@ApiModelProperty(value="批号",required=false)
	private String batch_number;
}
