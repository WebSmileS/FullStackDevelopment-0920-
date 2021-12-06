package com.yrt.project.modular.product.domain;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DealerProduct implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "经销商内部编号")
	private Long dealer_inner_sn;
	@ApiModelProperty(value = "经销商名称")
	private String dealer_name;
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	/** 发布员工内部编号 */
	@ApiModelProperty(value = "发布员工内部编号 ")
	private Long create_employee_inner_sn;
	/** 自定义分类内部编号 */
	@ApiModelProperty(value = "自定义分类内部编号")
	private Long type_inner_sn;
	/** 自定义分类名称 */
	@ApiModelProperty(value = "自定义分类名称")
	private String type_name;
	/** 医疗器械分类目录编号 */
	@ApiModelProperty(value = "医疗器械分类目录编号")
	private Long code68_sn;
	/** 医疗器械分类目录名称 */
	@ApiModelProperty(value = "医疗器械分类目录名称")
	private String code68_name;
	/** 状态 */
	@ApiModelProperty(value = "状态")
	private Integer status;
	/** 存储条件 */
	@ApiModelProperty(value = "存储条件")
	private Integer storage_condition;
	/** 医保类型 */
	@ApiModelProperty(value = "医保类型")
	private Integer health_care_type;
	/** 已删除 */
	@ApiModelProperty(value = "已删除")
	private Integer is_delete;
	/** 名称* */
	@ApiModelProperty(value = "产品名称")
	private String name;
	/** 产品编号 */
	@ApiModelProperty(value = "自建产品编号")
	private String product_dealer_sn;
	/** 型号规格(总览) */
	@ApiModelProperty(value = "型号规格(总览)")
	private String specifications;
	/** 描述 */
	@ApiModelProperty(value = "描述")
	private String description;
	@ApiModelProperty(value = "拼音")
	private String pinyin;
}
