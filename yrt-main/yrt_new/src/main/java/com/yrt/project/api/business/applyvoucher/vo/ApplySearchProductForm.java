package com.yrt.project.api.business.applyvoucher.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApplySearchProductForm implements Serializable {

	private static final long serialVersionUID = 1L;

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
	
	/** 型号规格厂商自定编号 */
	@ApiModelProperty(value="型号规格厂商自定编号",required=false)
	private String specification_vendor_sn;
	
	/** 型号规格 */
	@ApiModelProperty(value="型号规格",required=false)
	private String specification;
}
