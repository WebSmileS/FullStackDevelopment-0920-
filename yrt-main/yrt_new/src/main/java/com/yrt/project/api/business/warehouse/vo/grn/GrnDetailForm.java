package com.yrt.project.api.business.warehouse.vo.grn;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.RefVoucherType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GrnDetailForm implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long detail_inner_sn;
	/** 产品厂商编号(可以是厂商ID，也可以是经销商ID) */
	private Long product_vendor_inner_sn;
	private String product_vendor_name;
	/** 产品内部编号 */
	private Long product_inner_sn;
	private String product_name;
	/** 型号规格内部编号 */
	private Long specification_inner_sn;
	private String specification;
	/** 单位内部编号 */
	private Long unit_inner_sn;
	private String unit_name;
	/** 登记表内部编号 */
	private Long mdrf_inner_sn;
	private String mdrf_sn;
	/** 单价 */
	private BigDecimal unit_price;
	/** 数量 */
	private BigDecimal quantity;
	private BigDecimal quantity_old;
	/**税率 */
	private BigDecimal rate;
	/** 生产日期 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date produce_date;
	/** 失效日期 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date overdue_date;
	/** 类别 */
	private Integer type;
	/** 排序号 */
	private Integer sort_number;
	/** 批号 */
	private String batch_number;
	/** 产品类型 0:经销商(自建)产品   1:厂商产品 */
	private Integer product_type;
	@ApiModelProperty(value = "医保编号")
	private String health_care_sn = "";
	/** 单据类型C */
	private RefVoucherType voucher_type_c=RefVoucherType.NONE;
	/** 单据内部编号C */
	private Long voucher_inner_sn_c;
	/** 单据明细内部编号C */
	private Long voucher_detail_inner_sn_c;
	
	/** 编辑动作 0-删除 1-新增 2-修改 */
	@ApiModelProperty(value = "编辑动作 0-删除 1-新增 2-修改")
	private Integer action;
	
	@ApiModelProperty(value = "验货明细ID")
	private Long inspection_detail_id;
	
	@ApiModelProperty(value="产品厂商ID,兼容平板的错误参数")
	private Long vendor_inner_sn;
	@ApiModelProperty(value="产品厂商名称,兼容平板的错误参数")
	private String vendor_name;

	/**
	 * 溢价比例
	 */
	@ApiModelProperty(value = "溢价比例，是一个百分比，产品的最终 售价=成本价+成本价*溢价比例，不传默认为0%")
	@NotNull(message = "溢价比例不能为空")
	private BigDecimal percent = BigDecimal.ZERO;

	@ApiModelProperty(value = "售价,如果产品的单价不为0，售价会以后端通过溢价比例算出来的值为准，如果单价为0，售价会以前端送的为准")
	private BigDecimal salesPrice;
}
