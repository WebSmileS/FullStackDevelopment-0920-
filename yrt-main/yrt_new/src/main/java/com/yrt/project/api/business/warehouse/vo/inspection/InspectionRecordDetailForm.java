package com.yrt.project.api.business.warehouse.vo.inspection;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.RefVoucherType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InspectionRecordDetailForm implements Serializable {
	
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
	@ApiModelProperty(value = "批号")
	private String batch_number;
	@ApiModelProperty(value = "到货数量")
	private BigDecimal arrival_quantity;
	@ApiModelProperty(value = "合格数量")
	private BigDecimal qualified_quantity;
	@ApiModelProperty(value = "不合格数量")
	private BigDecimal unqualified_quantity;
	@ApiModelProperty(value = "不合格原因")
	private String unqualified_reason;
	
	private Long mdrf_inner_sn;
	@ApiModelProperty(value = "医疗器械注册证号")
	private String mdrf_sn;
	@ApiModelProperty(value = "生产日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date produce_date;
	@ApiModelProperty(value = "失效日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date overdue_date;
	
	@ApiModelProperty(value = "产品类型 0:经销商(自建)产品   1:厂商产品")
	private Integer product_type;
	@ApiModelProperty(value = "单据类型")
	private RefVoucherType voucher_type_c = RefVoucherType.NONE;
	@ApiModelProperty(value = "单据内部编号")
	private Long voucher_inner_sn_c;
	
	@ApiModelProperty(value="收货单明细ID")
	private Long arrival_detail_id;
	
	@ApiModelProperty(hidden = true)
	private Long grn_id;
	@ApiModelProperty(hidden = true)
	private Long grn_detail_id;
}
