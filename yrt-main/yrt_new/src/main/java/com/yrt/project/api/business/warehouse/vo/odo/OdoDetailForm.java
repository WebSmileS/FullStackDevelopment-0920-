package com.yrt.project.api.business.warehouse.vo.odo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.RefVoucherType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OdoDetailForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="出库单明细ID",required=false)
	private Long detail_inner_sn;
	/** 产品厂商编号(可以是厂商ID，也可以是经销商ID) */
	@ApiModelProperty(value="产品厂商ID",required=true)
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
	@ApiModelProperty(value="产品规格型号ID",required=true)
	@NotNull(message = "产品规格型号ID不能为空")
	private Long specification_inner_sn;
	@ApiModelProperty(value="产品规格型号名称",required=true)
	@NotBlank(message = "产品规格型号名称不能为空")
	private String specification;
	/** 单位内部编号 */
	@ApiModelProperty(value="单位ID",required=true)
	@NotNull(message = "单位ID不能为空")
	private Long unit_inner_sn;
	@ApiModelProperty(value="产品单位名称",required=true)
	@NotBlank(message = "单位名称不能为空")
	private String unit_name;
	/** 登记表内部编号 */
	@ApiModelProperty(value="注册证ID",required=false)
	private Long mdrf_inner_sn;
	@ApiModelProperty(value="注册证号",required=false)
	private String mdrf_sn="";
	/** 单价 */
	@ApiModelProperty(value="单价",required=true)
	@NotNull(message = "单价不能为空")
	private BigDecimal unit_price;
	@ApiModelProperty(value="成本价",required=true)
	@NotNull(message = "成本价不能为空")
	private BigDecimal buying_price;
	/** 数量 */
	@ApiModelProperty(value="数量",required=true)
	@NotNull(message = "数量不能为空")
	private BigDecimal quantity;
	@ApiModelProperty(value = "税率",required=true)
	@NotNull(message = "税率不能为空")
	private BigDecimal rate;
	/** 生产日期 */
	@ApiModelProperty(value="生产日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date produce_date;
	/** 失效日期 */
	@ApiModelProperty(value="失效日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date overdue_date;
	/** 类别 */
	@ApiModelProperty(value="类别",required=false)
	private Integer type = 0;
	/** 排序号 */
	@ApiModelProperty(value="排序号",required=false)
	private Integer sort_number = 0;
	/** 批号 */
	@ApiModelProperty(value="批号",required=true)
	@NotBlank(message = "批号不能为空")
	private String batch_number;
	/**单据类型C*/
	@ApiModelProperty(value="单据类型 /v1/dict/refVoucherType",required=true)
	@NotNull(message = "单据类型不能为空")
	private RefVoucherType voucher_type_c = RefVoucherType.NONE;
	/**单据内部编号C*/
	@ApiModelProperty(value="单据ID",required=false)
	private Long voucher_inner_sn_c;
	/**单据明细内部编号C*/
	@ApiModelProperty(value="单据明细ID",required=false)
	private Long voucher_detail_inner_sn_c;
	/** 产品类型 0:经销商(自建)产品   1:厂商产品 */
	@ApiModelProperty(value="产品类型 0:经销商(自建)产品   1:厂商产品",required=true)
	@NotNull(message = "产品类型不能为空")
	private Integer product_type;
	@ApiModelProperty(value = "医保编号")
	private String health_care_sn = "";
}
