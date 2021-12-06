package com.yrt.project.api.business.dealer.vo.tmpproduct;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddTmpProductForm implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="数据来源 0来自平台;1来自医院;2来自经销商;3来自厂商",required=true)
	@NotNull(message = "数据来源不能为空")
	private Integer source;
	
	@ApiModelProperty(value="厂商ID",required=true)
	@NotNull(message = "厂商ID不能为空")
	private Long vendor_inner_sn;
	
//	@ApiModelProperty(value="产品ID",required=true)
//	@NotNull(message = "产品ID不能为空")
//	private Long product_inner_sn;
	
	@ApiModelProperty(value="自定义分类内部编号",required=false)
	private Long type_inner_sn;
	
	@ApiModelProperty(value="医疗器械68分类编号",required=false)
	private Long code68_sn;
	
	@ApiModelProperty(value="存储条件 0-其他 1-常温 2-保温 3-阴凉 4-冷藏 5-冷冻 6-无菌 7- 特储",required=true)
	@NotNull(message = "存储条件不能为空")
	private Integer storage_condition;
	
	@ApiModelProperty(value="医保类型 0-A型 1-B型 2-C型",required=true)
	@NotNull(message = "医保类型不能为空")
	private Integer health_care_type;
	
	@ApiModelProperty(value="产品类型 0-医疗器械 1-非医疗器械",required=true)
	@NotNull(message = "产品类型不能为空")
	private Integer type;
	
	@ApiModelProperty(value="名称",required=true)
	@NotBlank(message = "产品名称不能为空")
	private String name;
	
	@ApiModelProperty(value="型号规格(总览)",hidden = true)
	private String specifications = "";
	
	@ApiModelProperty(value="描述",required=false)
	private String description = "";
	
	@ApiModelProperty(value="操作 0-删除 1-新增 2-修改",required=true)
	@NotNull(message = "操作不能为空")
	private Integer operation;
	
	@ApiModelProperty(value="审核级别 0-一级 1-二级 2-三级 3-四级",required=true)
	@NotNull(message = "审核级别不能为空")
	private Integer audit_level;
	
	@ApiModelProperty(value="状态 2-草稿 3-待审核",required=true)
	@NotNull(message = "状态不能为空")
	private Integer status;
	/** 医疗器械分类目录类别:1-一类 2-二类 3-三类 */
	@ApiModelProperty(value="医疗器械分类目录类别:1-一类 2-二类 3-三类",required=false)
	private Integer code68_type;
}
