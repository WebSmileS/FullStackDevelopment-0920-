package com.yrt.project.api.platform.vo.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="前端新增产品的医疗器械注册证登记表信息",description="前端新增产品的医疗器械注册证登记表信息")
public class AddProductMDRFForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private Map<String, Object> pInfo = new HashMap<String, Object>();

	/** 产品名称 */
	@ApiModelProperty(value="产品名称",required=false)
	private String product_name;
	private String product_name_old;
	
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
	
	@ApiModelProperty(value="注册证图片",required=false)
	private List<ProductMDRFImageForm> images = new ArrayList<ProductMDRFImageForm>();
	
	public boolean hasProductInfo() {
		if(StringUtils.isNotEmpty(product_name) && StringUtils.isNotEmpty(product_name_old)) {
			pInfo.put("name", product_name.trim());
		}
		return StringUtils.isNotEmpty(pInfo);
	}
	
	public Map<String, Object> getProductInfo(){
		return pInfo;
	}
}
