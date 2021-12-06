package com.yrt.project.modular.warehouse.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SasDetail implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "机构内部编号")
	private Long org_id;
	@ApiModelProperty(value = "仓库内部编号")
	private Long warehouse_inner_sn;
	@ApiModelProperty(value = "储位分配单内部编号")
	private Long sas_inner_sn;
	@ApiModelProperty(value = "明细内部编号")
	private Long detail_inner_sn;
	@ApiModelProperty(value = "库存内部编号")
	private Long inventory_inner_sn;
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
	@ApiModelProperty(value = "产品包装单位内部编号")
	private Long unit_inner_sn;
	@ApiModelProperty(value = "产品单位名称")
	private String unit_name;
	@ApiModelProperty(value = "存储位置内部编号")
	private Long location_inner_sn;
	@ApiModelProperty(value = "存储位置名称")
	private String location;
	@ApiModelProperty(value = "状态")
	private Integer status;
	@ApiModelProperty(value = "应放数量")
	private BigDecimal expect_quantity;
	@ApiModelProperty(value = "实放数量")
	private BigDecimal real_quantity;
	@ApiModelProperty(value = "排序号")
	private Integer sort_number;
	@ApiModelProperty(value = "批号")
	private String batch_number;
	@ApiModelProperty(value = "描述")
	private String description;
	
	@ApiModelProperty(value = "产品类型 0:经销商(自建)产品   1:厂商产品")
	private Integer product_type;
	@ApiModelProperty(value = "入库单明细内部编号")
	private Long grn_detail_inner_sn;
}
