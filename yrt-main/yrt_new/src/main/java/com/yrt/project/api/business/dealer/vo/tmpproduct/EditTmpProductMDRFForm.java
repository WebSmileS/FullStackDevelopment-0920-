package com.yrt.project.api.business.dealer.vo.tmpproduct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditTmpProductMDRFForm implements Serializable {

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
	
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message = "产品ID不能为空")
	private Long product_inner_sn;
	
	@ApiModelProperty(value="注册证ID",required=true)
	@NotNull(message = "注册证ID不能为空")
	private Long mdrf_inner_sn;
	
	/** 颁发日期 */
	@ApiModelProperty(value="颁发日期",required=true)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "颁发日期不能为空")
	private Date issue_date;
	/** 开始日期 */
	@ApiModelProperty(value="开始日期",required=true)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "开始日期不能为空")
	private Date begin_date;
	/** 截止日期 */
	@ApiModelProperty(value="截止日期",required=true)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "截止日期不能为空")
	private Date end_date;
	/** 注册号 */
	@ApiModelProperty(value="注册号",required=true)
	@NotBlank(message = "注册号不能为空")
	private String register_sn;
	/** 发证部门 */
	@ApiModelProperty(value="发证部门",required=false)
	private String issue_organization = "";
	/** 产品标准 */
	@ApiModelProperty(value="产品标准",required=false)
	private String standard = "";
	/** 生产地址 */
	@ApiModelProperty(value="生产地址",required=false)
	private String production_address = "";
	/** 性能结构及组成 */
	@ApiModelProperty(value="性能结构及组成",required=false)
	private String performance_structure = "";
	/** 适用范围 */
	@ApiModelProperty(value="适用范围",required=false)
	private String application_range = "";
	/** 禁忌症 */
	@ApiModelProperty(value="禁忌症",required=false)
	private String contraindication = "";
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description = "";
	
	/** 型号规格(总览) */
	@ApiModelProperty(value="型号规格(总览)",required=false)
	private String specifications = "";
	
	@ApiModelProperty(value="操作 0-删除 1-新增 2-修改",required=true)
	@NotNull(message = "操作不能为空")
	private Integer operation;
	
	@ApiModelProperty(value="审核级别 0-一级 1-二级 2-三级 3-四级",required=true)
	@NotNull(message = "审核级别不能为空")
	private Integer audit_level;
	
	@ApiModelProperty(value="状态 2-草稿 3-待审核",required=true)
	@NotNull(message = "状态不能为空")
	private Integer status;
	
	@ApiModelProperty(value="注册证图片",required=false)
	private List<TmpProductMDRFImageForm> images = new ArrayList<>();
	
}
