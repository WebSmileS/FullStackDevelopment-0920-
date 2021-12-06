package com.yrt.project.api.business.order.vo.record;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.yrt.common.dict.RefVoucherType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SaleRecordDetailForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="厂商内部编号如果是经销商自建产品则是经销商ID",required=true)
	@NotNull(message = "产品厂商ID不能为空")
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value="产品厂商名称",required=true)
	@NotBlank(message = "产品厂商名称不能为空")
	private String product_vendor_name;
	
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message = "产品ID不能为空")
	private Long product_inner_sn;
	@ApiModelProperty(value="产品名称",required=true)
	@NotBlank(message = "产品名称不能为空")
	private String product_name;
	
	@ApiModelProperty(value="型号规格ID",required=true)
	@NotNull(message = "型号规格ID不能为空")
	private Long specification_inner_sn;
	@ApiModelProperty(value="型号规格名称",required=true)
	@NotBlank(message = "型号规格名称不能为空")
	private String specification;
	
	@ApiModelProperty(value="单位ID",required=true)
	@NotNull(message = "单位ID不能为空")
	private Long unit_inner_sn;
	@ApiModelProperty(value="包装单位名称",required=true)
	@NotBlank(message = "包装单位名称不能为空")
	private String unit;
	
	@ApiModelProperty(value="数量",required=false)
	private BigDecimal quantity = new BigDecimal(0);
	@ApiModelProperty(value="单价",required=false)
	private BigDecimal unit_price = new BigDecimal(0);
	@ApiModelProperty(value="折扣",required=false)
	private BigDecimal discount = new BigDecimal(1);
	@ApiModelProperty(value="税率",required=false)
	private BigDecimal rate = new BigDecimal(0);
	@ApiModelProperty(value="排序号",required=false)
	private Integer sort_number = 0;
	@ApiModelProperty(value="批号",required=false)
	private String batch_number;
	@ApiModelProperty(value="注册证ID",required=false)
	private Long mdrf_inner_sn;
	@ApiModelProperty(value="注册证号",required=false)
	private String mdrf_sn;
	@ApiModelProperty(value = "医保编号")
	private String health_care_sn = "";
	@ApiModelProperty(value = "阳光流水号")
	private String sun_sn = "";
	
	@ApiModelProperty(value="产品类型 0:经销商(自建)产品   1:厂商产品",required=true)
	@NotNull(message = "产品类型不能为空")
	private Integer product_type;
	
	@ApiModelProperty(value="销售单关联单据类型",hidden = true)
	private RefVoucherType voucher_type = RefVoucherType.NONE;
}
