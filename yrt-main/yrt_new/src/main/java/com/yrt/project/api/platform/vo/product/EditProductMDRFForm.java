package com.yrt.project.api.platform.vo.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="前端编辑产品的医疗器械注册证登记表信息",description="前端编辑产品的医疗器械注册证登记表信息")
public class EditProductMDRFForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private Map<String, Object> pInfo = new HashMap<String, Object>();
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	private Map<String, Object> pMDRF = new HashMap<String, Object>();
	
	/** 产品名称 */
	@ApiModelProperty(value="产品名称",required=false)
	private String product_name;
	private String product_name_old;
	
	/** 颁发日期 */
	@ApiModelProperty(value="颁发日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date issue_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date issue_date_old;
	/** 开始日期 */
	@ApiModelProperty(value="开始日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date_old;
	/** 截止日期 */
	@ApiModelProperty(value="截止日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date_old;
	/** 注册号 */
	@ApiModelProperty(value="注册号",required=false)
	private String register_sn;
	private String register_sn_old;
	/** 发证部门 */
	@ApiModelProperty(value="发证部门",required=false)
	private String issue_organization;
	private String issue_organization_old;
	/** 产品标准 */
	@ApiModelProperty(value="产品标准",required=false)
	private String standard;
	private String standard_old;
	/** 生产地址 */
	@ApiModelProperty(value="生产地址",required=false)
	private String production_address;
	private String production_address_old;
	/** 性能结构及组成 */
	@ApiModelProperty(value="性能结构及组成",required=false)
	private String performance_structure;
	private String performance_structure_old;
	/** 适用范围 */
	@ApiModelProperty(value="适用范围",required=false)
	private String application_range;
	private String application_range_old;
	/** 禁忌症 */
	@ApiModelProperty(value="禁忌症",required=false)
	private String contraindication;
	private String contraindication_old;
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description;
	private String description_old;
	
	/** 型号规格(总览) */
	@ApiModelProperty(value="型号规格(总览)",required=false)
	private String specifications;
	private String specifications_old;
	
	@ApiModelProperty(value="注册证图片",required=true)
	private List<ProductMDRFImageForm> images = new ArrayList<ProductMDRFImageForm>();
	
	public boolean hasProductInfo() {
		if(StringUtils.isNotEmpty(product_name)) {
			pInfo.put("name", product_name.trim());
		}
		return StringUtils.isNotEmpty(pInfo);
	}
	
	public Map<String, Object> getProductInfo(){
		return pInfo;
	}
	
	public boolean hasProductMDRF() {
		if(StringUtils.isNotNull(issue_date) || StringUtils.isNotNull(issue_date_old)) {
			pMDRF.put("issue_date", issue_date);
		}
		if(StringUtils.isNotNull(begin_date) || StringUtils.isNotNull(begin_date_old)) {
			pMDRF.put("begin_date", begin_date);
		}
		if(StringUtils.isNotNull(end_date) || StringUtils.isNotNull(end_date_old)) {
			pMDRF.put("end_date", end_date);
		}
		if(StringUtils.isNotEmpty(register_sn) || StringUtils.isNotEmpty(register_sn_old)) {
			pMDRF.put("register_sn", register_sn);
		}
		if(StringUtils.isNotEmpty(standard) || StringUtils.isNotEmpty(standard_old)) {
			pMDRF.put("standard", standard);
		}
		if(StringUtils.isNotEmpty(issue_organization) || StringUtils.isNotEmpty(issue_organization_old)) {
			pMDRF.put("issue_organization", issue_organization);
		}
		if(StringUtils.isNotEmpty(production_address) || StringUtils.isNotEmpty(production_address_old)) {
			pMDRF.put("production_address", production_address);
		}
		if(StringUtils.isNotEmpty(performance_structure) || StringUtils.isNotEmpty(performance_structure_old)) {
			pMDRF.put("performance_structure", performance_structure);
		}
		if(StringUtils.isNotEmpty(application_range) || StringUtils.isNotEmpty(application_range_old)) {
			pMDRF.put("application_range", application_range);
		}
		if(StringUtils.isNotEmpty(contraindication) || StringUtils.isNotEmpty(contraindication_old)) {
			pMDRF.put("contraindication", contraindication);
		}
		if(StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(description_old)) {
			pMDRF.put("description", description);
		}
		if(StringUtils.isNotEmpty(specifications) || StringUtils.isNotEmpty(specifications_old)) {
			pMDRF.put("specifications", specifications);
		}
		return StringUtils.isNotEmpty(pMDRF);
	}
	
	public Map<String, Object> getProductMDRF(){
		return pMDRF;
	}
}
