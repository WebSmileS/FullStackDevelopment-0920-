package com.yrt.project.api.platform.vo.product;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.project.modular.product.domain.ProductMDRFImage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ViewProductMDRFForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 生产企业名称* */
	@ApiModelProperty(value="生产企业名称",required=false)
	private String vendor_name;
	
	/** 企业注册地址 */
	@ApiModelProperty(value="企业注册地址",required=false)
	private String address;
	
	/** 产品名称 */
	@ApiModelProperty(value="产品名称",required=false)
	private String product_name;
	
	/** 产品类型 0-医疗器械 1-非医疗器械 */
	@ApiModelProperty(value="产品类型 0-医疗器械 1-非医疗器械",required=false)
	private Integer type;
	
	/** 型号规格(总览) */
	@ApiModelProperty(value="型号规格(总览)",required=false)
	private String specifications;
	
	/** 颁发日期 */
	@ApiModelProperty(value="颁发日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date issue_date;
	/** 开始日期 */
	@ApiModelProperty(value="开始日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	/** 截止日期 */
	@ApiModelProperty(value="截止日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
	/** 注册号 */
	@ApiModelProperty(value="注册号",required=false)
	private String register_sn;
	/** 发证部门 */
	@ApiModelProperty(value="发证部门",required=false)
	private String issue_organization;
	/** 产品标准 */
	@ApiModelProperty(value="产品标准",required=false)
	private String standard;
	/** 生产地址 */
	@ApiModelProperty(value="生产地址",required=false)
	private String production_address;
	/** 性能结构及组成 */
	@ApiModelProperty(value="性能结构及组成",required=false)
	private String performance_structure;
	/** 适用范围 */
	@ApiModelProperty(value="适用范围",required=false)
	private String application_range;
	/** 禁忌症 */
	@ApiModelProperty(value="禁忌症",required=false)
	private String contraindication;
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description;
	
	/** 相关图片 */
	@ApiModelProperty(value="相关图片列表")
	private List<ProductMDRFImage> images;
}
