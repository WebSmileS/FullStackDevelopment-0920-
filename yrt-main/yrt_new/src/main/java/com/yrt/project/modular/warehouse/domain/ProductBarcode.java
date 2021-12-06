package com.yrt.project.modular.warehouse.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductBarcode implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "条码编号")
	private String code;
	@ApiModelProperty(value = "机构内部编号")
	private String orgId;
	@ApiModelProperty(value = "机构类型")
	private String orgType;
	@ApiModelProperty(value = "相关机构内部编号")
	private Long organization_inner_sn;
	@ApiModelProperty(value = "(产品)厂商名称")
	private String organization_name;
	@ApiModelProperty(value = "(产品)厂商内部编号")
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	@ApiModelProperty(value = "型号规格内部编号")
	private Long specification_inner_sn;
	@ApiModelProperty(value = "单位内部编号")
	private Long unit_inner_sn;
	@ApiModelProperty(value = "登记表内部编号")
	private Long mdrf_inner_sn;
	@ApiModelProperty(value = "仓库内部编号")
	private Long warehouse_inner_sn;
	@ApiModelProperty(value = "库存内部编号")
	private Long inventory_inner_sn;
	@ApiModelProperty(value = "相关机构类别")
	private Long organization_type;
	@ApiModelProperty(value = "入库单内部编号")
	private Long grn_inner_sn;
	@ApiModelProperty(value = "入库单明细内部编号")
	private Long grn_detail_inner_sn;
	@ApiModelProperty(value = "验货单内部编号")
	private Long pp_inner_sn;
	@ApiModelProperty(value = "验货单明细内部编号")
	private Long pp_detail_inner_sn;
	@ApiModelProperty(value = "条码类别")
	private Integer barcode_type;
	@ApiModelProperty(value = "产品数")
	private BigDecimal product_number;
	@ApiModelProperty(value = "已确认产品数")
	private BigDecimal confirm_product_number;
	@ApiModelProperty(value = "剩余产品数")
	private BigDecimal surplus_product_number;
	@ApiModelProperty(value = "生产日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date produce_date;
	@ApiModelProperty(value = "失效日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date overdue_date;
	@ApiModelProperty(value = "生成时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date create_time;
	@ApiModelProperty(value = "批号")
	private String batch_number;
	@ApiModelProperty(value = "(产品)厂商名称")
	private String product_vendor_name;
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	@ApiModelProperty(value = "型号规格")
	private String specification;
	@ApiModelProperty(value = "单位")
	private String unit_name;
	@ApiModelProperty(value = "医疗器械注册证号")
	private String mdrf_sn;
	@ApiModelProperty(value = "产品类型 0:经销商(自建)产品   1:厂商产品")
	private Integer product_type;
	@ApiModelProperty(value="单据类型",required=true)
	private BillsType billsType;
}
