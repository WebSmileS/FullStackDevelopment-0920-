package com.yrt.project.modular.warehouse.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InventoryProduct implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "机构编号")
	private Long org_id;
	@ApiModelProperty(value = "仓库内部编号")
	private Long warehouse_inner_sn;
	@ApiModelProperty(value = "仓库名称")
	private String warehouse_name;
	@ApiModelProperty(value = "厂商内部编号")
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	@ApiModelProperty(value = "型号规格内部编号")
	private Long specification_inner_sn;
	@ApiModelProperty(value = "单位内部编号")
	private Long unit_inner_sn;
	@ApiModelProperty(value = "库存内部编号")
	private Long inventory_inner_sn;
	@ApiModelProperty(value = "登记表内部编号(自建产品无)")
	private Long mdrf_inner_sn;
	@ApiModelProperty(value = "单价")
	private BigDecimal unit_price;
	@ApiModelProperty(value = "数量")
	private BigDecimal quantity;
	@ApiModelProperty(value = "生产日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date produce_date;
	@ApiModelProperty(value = "失效日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date overdue_date;
	@ApiModelProperty(value = "入库时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date in_time;
	@ApiModelProperty(value = "状态")
	private Integer status;
	@ApiModelProperty(value = "放置状态")
	private Integer place_status;
	@ApiModelProperty(value = "类别")
	private Integer type;
	@ApiModelProperty(value = "批号")
	private String batch_number;
	@ApiModelProperty(value = "厂商名称")
	private String product_vendor_name;
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	@ApiModelProperty(value = "型号规格")
	private String specification;
	@ApiModelProperty(value = "单位")
	private String unit;
	@ApiModelProperty(value = "医疗器械注册证号(自建产品无)")
	private String mdrf_sn;
	
	@ApiModelProperty(value = "产品类型 0:经销商(自建)产品   1:厂商产品")
	private Integer product_type;
	@ApiModelProperty(value = "是否过期")
	private boolean isExpire = false;
}
