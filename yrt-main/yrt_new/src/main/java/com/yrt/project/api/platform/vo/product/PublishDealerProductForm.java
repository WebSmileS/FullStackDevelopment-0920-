package com.yrt.project.api.platform.vo.product;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="前端新增产品信息",description="前端新增产品信息")
public class PublishDealerProductForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 经销商ID内部编号 */
	@ApiModelProperty(value="经销商ID",required=true)
	@NotNull(message = "经销商ID不能为空")
	private Long dealer_inner_sn;
	/** 自建产品ID内部编号 */
	@ApiModelProperty(value="自建产品ID",required=true)
	@NotNull(message = "自建产品ID不能为空")
	private Long product_inner_sn;
	/** 厂商内部编号 */
	@ApiModelProperty(value="厂商ID",required=true)
	@NotNull(message = "厂商不能为空")
	private Long vendor_inner_sn;
	/** 自定义分类内部编号 */
	@ApiModelProperty(value="自定义分类内部编号",required=false)
	private Long type_inner_sn;
	/** 医疗器械分类目录编号 */
	@ApiModelProperty(value="医疗器械68分类编号",required=false)
	private Long code68_sn;
	/** 存储条件 */
	@ApiModelProperty(value="存储条件 0-其他 1-常温 2-保温 3-阴凉 4-冷藏 5-冷冻 6-无菌 7- 特储",required=true)
	@NotNull(message = "存储条件不能为空")
	private Integer storage_condition;
	/** 医保类型 */
	@ApiModelProperty(value="医保类型 0-A型 1-B型 2-C型",required=true)
	@NotNull(message = "医保类型不能为空")
	private Integer health_care_type;
	/** 产品类型 0-医疗器械 1-非医疗器械 */
	@ApiModelProperty(value="产品类型 0-医疗器械 1-非医疗器械",required=true)
	@NotNull(message = "产品类型不能为空")
	private Integer type;
	/** 名称* */
	@ApiModelProperty(value="名称",required=true)
	@NotBlank(message = "产品名称不能为空")
	private String name;
	/** 型号规格(总览) */
	@ApiModelProperty(value="型号规格(总览)",required=false)
	private String specifications = "";
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description = "";
	/** 医疗器械分类目录类别:1-一类 2-二类 3-三类 */
	@ApiModelProperty(value="医疗器械分类目录类别:1-一类 2-二类 3-三类",required=false)
	private Integer code68_type;
}
