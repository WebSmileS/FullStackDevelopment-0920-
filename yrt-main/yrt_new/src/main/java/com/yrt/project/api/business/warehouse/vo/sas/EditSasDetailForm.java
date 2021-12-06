package com.yrt.project.api.business.warehouse.vo.sas;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class EditSasDetailForm implements Serializable {

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
	/** 库存内部编号 */
	private Long inventory_inner_sn;
	/** 储位置内部编号 */
	private Long location_inner_sn;
	private String location;
	/** 应放数量 */
	private BigDecimal quantity;
	/** 排序号 */
	private Integer sort_number;
	/** 批号 */
	private String batch_number;
	/** 产品类型 0:经销商(自建)产品   1:厂商产品 */
	private Integer product_type;
	/** 编辑动作 0-删除 1-新增 2-修改 */
	private Integer action;
}
