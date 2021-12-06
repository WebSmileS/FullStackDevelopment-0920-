package com.yrt.project.api.platform.vo.product;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PendingAuditProductForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="产品编辑机构ID",required=true)
	@NotNull(message = "产品编辑机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="数据来源 0来自平台;1来自医院;2来自经销商;3来自厂商",required=true)
	@NotNull(message = "数据来源不能为空")
	private Integer source;
	
	@ApiModelProperty(value="厂商ID",required=true)
	@NotNull(message = "厂商ID不能为空")
	private Long vendor_inner_sn;
	
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message = "产品ID不能为空")
	private Long product_inner_sn;
	
	@ApiModelProperty(value="产品图片ID",required=false)
	private Long image_inner_sn;
	
	@ApiModelProperty(value="产品规格型号ID",required=false)
	private Long specification_inner_sn;
	
	@ApiModelProperty(value="产品医疗注册证ID",required=false)
	private Long mdrf_inner_sn;
	
	@ApiModelProperty(value="操作 0-删除 1-新增 2-修改",required=true)
	@NotNull(message = "操作不能为空")
	private Integer operation;
	
	@ApiModelProperty(value="审批类型 0-产品 1-规格型号 2-注册证登记表 3-产品图片",required=true)
	@NotNull(message = "审批类型不能为空")
	private Integer audit_type;
}