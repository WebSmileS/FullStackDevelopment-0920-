package com.yrt.project.api.business.warehouse.vo.receipt;

import java.io.Serializable;
import java.math.BigDecimal;

import com.yrt.common.dict.RefVoucherType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ArrivalRecordDetailForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(hidden = true)
	private Long id;
	
	@ApiModelProperty(value = "厂商内部编号 ")
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value = "厂商名称")
	private String product_vendor_name;
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	@ApiModelProperty(value = "型号规格内部编号")
	private Long specification_inner_sn;
	@ApiModelProperty(value = "型号规格")
	private String specification;
	@ApiModelProperty(value = "包装单位内部编号")
	private Long unit_inner_sn;
	@ApiModelProperty(value = "包装单位名称")
	private String unit;
	@ApiModelProperty(value = "订单数量")
	private BigDecimal order_quantity;
	@ApiModelProperty(value = "到货数量")
	private BigDecimal arrival_quantity;
	@ApiModelProperty(value = "批号")
	private String batch_number;
	@ApiModelProperty(value = "产品类型 0:经销商(自建)产品   1:厂商产品")
	private Integer product_type;
	@ApiModelProperty(value = "单据类型")
	private RefVoucherType voucher_type_c = RefVoucherType.NONE;
	@ApiModelProperty(value = "单据内部编号")
	private Long voucher_inner_sn_c;
	
	@ApiModelProperty(hidden = true)
	private Long inspection_detail_id;
}
