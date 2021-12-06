package com.yrt.project.modular.product.domain;

import java.io.Serializable;
import java.util.List;

import com.yrt.project.api.platform.vo.product.ViewProductMDRFForm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductDetail implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "厂商内部编号 ")
	private Long vendor_inner_sn;
	@ApiModelProperty(value = "厂商名称")
	private String vendor_name;
	@ApiModelProperty(value = "数据来源 ")
	private Integer data_source;
	@ApiModelProperty(value = "产品类型 0-医疗器械 1-非医疗器械")
	private Integer type;
	@ApiModelProperty(value = "医疗器械分类目录类别:1-一类 2-二类 3-三类 ")
	private Integer code68_type;
	@ApiModelProperty(value = "自定义分类内部编号")
	private Long type_inner_sn;
	@ApiModelProperty(value = "自定义分类名称")
	private String type_name;
	@ApiModelProperty(value = "医疗器械分类目录编号")
	private Long code68_sn;
	@ApiModelProperty(value = "医疗器械分类目录名称")
	private String code68_name;
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	@ApiModelProperty(value = "存储条件")
	private Integer storage_condition;
	@ApiModelProperty(value = "医保类型")
	private Integer health_care_type;
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	@ApiModelProperty(value = "自建产品编号")
	private String product_dealer_sn;
	@ApiModelProperty(value = "型号规格(总览)")
	private String specifications;
	@ApiModelProperty(value = "描述")
	private String description;
	
	List<ProductSpecification> specificationList;
	List<ViewProductMDRFForm> MDRFList;
	List<ProductRelImage> productImageList;
}
