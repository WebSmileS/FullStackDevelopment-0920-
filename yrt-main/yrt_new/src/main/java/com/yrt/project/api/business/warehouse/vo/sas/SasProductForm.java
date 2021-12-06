package com.yrt.project.api.business.warehouse.vo.sas;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SasProductForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="仓库ID",required=true)
	@NotNull(message = "仓库不能为空")
	private Long warehouse_inner_sn;
	
	@ApiModelProperty(value="入库单ID",required=true)
	@NotNull(message = "入库单ID不能为空")
	private Long grn_inner_sn;
	
	@ApiModelProperty(value="入库单明细ID",required=false)
	private Long grn_detail_inner_sn;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
	
	@ApiModelProperty(value="产品厂商ID",required=false)
	private Long product_vendor_inner_sn;
	
	@ApiModelProperty(value="产品ID",required=false)
	private Long product_inner_sn;
	
	@ApiModelProperty(value="规格型号ID",required=false)
	private Long specification_inner_sn;
}
