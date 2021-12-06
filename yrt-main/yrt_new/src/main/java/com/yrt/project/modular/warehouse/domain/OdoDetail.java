package com.yrt.project.modular.warehouse.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.RefVoucherType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OdoDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "机构内部编号")
	private Long org_id;
	@ApiModelProperty(value = "出库单内部编号")
	private Long odo_inner_sn;
	@ApiModelProperty(value = "明细内部编号")
	private Long detail_inner_sn;
	
	@ApiModelProperty(value = "厂商内部编号 ")
	private Long vendor_inner_sn;
	@ApiModelProperty(value = "厂商名称")
	private String vendor_name;
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	@ApiModelProperty(value = "型号规格内部编号")
	private Long specification_inner_sn;
	@ApiModelProperty(value = "型号规格")
	private String specification;
	@ApiModelProperty(value = "产品包装单位内部编号")
	private Long unit_inner_sn;
	@ApiModelProperty(value = "产品单位名称")
	private String unit_name;
	@ApiModelProperty(value = "登记表内部编号")
	private Long mdrf_inner_sn;
	@ApiModelProperty(value = "医疗器械注册证号")
	private String mdrf_sn;
	@ApiModelProperty(value = "数量")
	private BigDecimal quantity;
	@ApiModelProperty(value = "生产日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date produce_date;
	@ApiModelProperty(value = "失效日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date overdue_date;
	@ApiModelProperty(value = "排序号")
	private Integer sort_number;
	@ApiModelProperty(value = "批号")
	private String batch_number;
	@ApiModelProperty(value = "产品类型 0:经销商(自建)产品   1:厂商产品")
	private Integer product_type;
	@ApiModelProperty(value = "单价")
	private BigDecimal unit_price;
	@ApiModelProperty(value="成本价")
	private BigDecimal buying_price = BigDecimal.ZERO;
	@ApiModelProperty(value = "税率")
	private BigDecimal rate;
	@ApiModelProperty(value = "类别")
	private Integer type;
	@ApiModelProperty(value = "医保编号")
	private String health_care_sn;
	/**单据类型C*/
	@ApiModelProperty(value = "单据类型")
	private RefVoucherType voucher_type_c;
	/**单据内部编号C*/
	@ApiModelProperty(value = "单据内部编号")
	private Long voucher_inner_sn_c;
	/**单据明细内部编号C*/
	@ApiModelProperty(value = "单据明细内部编号")
	private Long voucher_detail_inner_sn_c;

	@ApiModelProperty(value = "产品编号，仅厂商产品有")
	private String specification_vendor_sn;

}
