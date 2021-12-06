package com.yrt.project.modular.applyvoucher.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class HospitalGrantVoucherDetail implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/** 医院内部编号 */
	@ApiModelProperty(value = "医院内部编号")
	private Long hospital_inner_sn;
	@ApiModelProperty(value = "医院名称")
	private String hospital_name;
	/** 发放单内部编号 */
	@ApiModelProperty(value = "发放单内部编号")
	private Long gv_inner_sn;
	/** 明细内部编号 */
	@ApiModelProperty(value = "明细内部编号")
	private Long detail_inner_sn;
	/** (产品)厂商内部编号 */
	@ApiModelProperty(value = "(产品)厂商内部编号")
	private Long vendor_inner_sn;
	/** 产品内部编号 */
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	/** 型号规格内部编号 */
	@ApiModelProperty(value = "型号规格内部编号")
	private Long specification_inner_sn;
	/** 单位内部编号 */
	@ApiModelProperty(value = "单位内部编号")
	private Long unit_inner_sn;
	/** 排序号 */
	@ApiModelProperty(value = "排序号")
	private Integer sort_number;
	/** 批号 */
	@ApiModelProperty(value = "批号")
	private String batch_number;
	/** 批准发放数量 */
	@ApiModelProperty(value = "批准发放数量")
	private BigDecimal grant_quantity;
	/** 领取数量 */
	@ApiModelProperty(value = "领取数量")
	private BigDecimal get_quantity;
	/** (产品)厂商名称(冗余) */
	@ApiModelProperty(value = "(产品)厂商名称")
	private String vendor_name;
	/** 产品名称(冗余) */
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	/** 型号规格(冗余) */
	@ApiModelProperty(value = "型号规格")
	private String specification;
	/** 单位(冗余) */
	@ApiModelProperty(value = "单位")
	private String unit_name;
	@ApiModelProperty(value = "产品类型 0:经销商(自建)产品   1:厂商产品")
	private Integer product_type;
	@ApiModelProperty(value = "图片")
	private String image;
	@ApiModelProperty(value = "仓库内部编号")
	private Long warehouse_inner_sn;
	@ApiModelProperty(value = "仓库名称")
	private String warehouse_name;
	@ApiModelProperty(value = "生产日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private String produce_date;
	@ApiModelProperty(value = "失效日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private String overdue_date;
	@ApiModelProperty(value = "状态")
	private Integer status;

	@ApiModelProperty(value = "溢价比例")
	private BigDecimal percent;

	@ApiModelProperty(value = "售价")
	private BigDecimal salesPrice;
	
}
