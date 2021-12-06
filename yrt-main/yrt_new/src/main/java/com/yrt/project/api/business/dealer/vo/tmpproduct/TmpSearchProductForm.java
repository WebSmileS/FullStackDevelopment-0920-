package com.yrt.project.api.business.dealer.vo.tmpproduct;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="前端搜索产品信息",description="前端搜索产品信息")
public class TmpSearchProductForm implements Serializable {

	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="数据来源 0来自平台;1来自医院;2来自经销商;3来自厂商",required=true)
	@NotNull(message = "数据来源不能为空")
	private Integer source;
	
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
	
	/** 型号规格厂商自定编号 */
//	@ApiModelProperty(value="型号规格厂商自定编号",required=false)
//	private String specification_vendor_sn;
}
