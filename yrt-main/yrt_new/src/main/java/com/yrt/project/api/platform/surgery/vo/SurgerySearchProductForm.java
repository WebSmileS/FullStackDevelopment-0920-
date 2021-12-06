package com.yrt.project.api.platform.surgery.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="前端搜索产品信息",description="前端搜索产品信息")
public class SurgerySearchProductForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
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
}
