package com.yrt.project.api.business.order.vo.record;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SaleRecordSaveOdoDetailForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="销售单明细ID",required=true)
	private Long sl_detail_inner_sn;
	@ApiModelProperty(value="产品厂商ID",required=true)
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
	@ApiModelProperty(value="产品规格型号ID",required=true)
	@NotNull(message = "产品规格型号ID不能为空")
	private Long specification_inner_sn;
	@ApiModelProperty(value="产品规格型号名称",required=true)
	@NotBlank(message = "产品规格型号名称不能为空")
	private String specification;
	@ApiModelProperty(value="单位ID",required=true)
	@NotNull(message = "单位ID不能为空")
	private Long unit_inner_sn;
	@ApiModelProperty(value="产品单位名称",required=true)
	@NotBlank(message = "单位名称不能为空")
	private String unit_name;
	@ApiModelProperty(value="注册证ID",required=false)
	private Long mdrf_inner_sn;
	@ApiModelProperty(value="注册证号",required=false)
	private String mdrf_sn="";
	@ApiModelProperty(value="单价",required=true)
	@NotNull(message = "单价不能为空")
	private BigDecimal unit_price;
	@ApiModelProperty(value="数量",required=true)
	@NotNull(message = "数量不能为空")
	private BigDecimal quantity;
	@ApiModelProperty(value="生产日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date produce_date;
	@ApiModelProperty(value="失效日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date overdue_date;
	@ApiModelProperty(value="类别",required=false)
	private Integer type = 0;
	@ApiModelProperty(value="排序号",required=false)
	private Integer sort_number = 0;
	@ApiModelProperty(value="批号",required=true)
	@NotBlank(message = "批号不能为空")
	private String batch_number;
	@ApiModelProperty(value="产品类型 0:经销商(自建)产品   1:厂商产品",required=true)
	@NotNull(message = "产品类型不能为空")
	private Integer product_type;
	
}
