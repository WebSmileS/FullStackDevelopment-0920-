package com.yrt.project.api.business.warehouse.vo.odo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchOdoProductForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message="当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="相关机构ID",required=true)
	@NotNull(message="相关机构ID不能为空")
	private Long organization_inner_sn;
	
	/** 名称* */
	@ApiModelProperty(value="产品名称",required=false)
	private String name;

	/** 厂商ID */
	@ApiModelProperty(value="厂商ID",required=false)
	private Long vendor_inner_sn;
	
	/** 自定义类型编号 */
	@ApiModelProperty(value="自定义类型编号",required=false)
	private Long type_inner_sn;
	
	/** 68分类编号 */
	@ApiModelProperty(value="68分类编号",required=false)
	private Long code68_sn;
	
	/** 规格型号* */
	@ApiModelProperty(value="规格型号",required=false)
	private String specification;
	
	/** 产品编码* */
	@ApiModelProperty(value="产品编码",required=false)
	private String specification_vendor_sn;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
	
	@ApiModelProperty(value="仓库",required=false)
	private Long warehouse_inner_sn;

	@ApiModelProperty(value="分类层级，新需求新增出库单库存数量展示，不传默认是0，传1表示按仓库来作二级划分",required=false)
	private int level;
}
