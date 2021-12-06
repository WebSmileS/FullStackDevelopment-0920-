/**
 * 
 */
package com.yrt.project.modular.external.domain;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Runner
 *
 */
@Data
public class ExternalProductInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="厂商ID",required=false)
	private Long vendor_inner_sn;

	@ApiModelProperty(value="厂商名称",required=false)
	private String vendor_name;
	
	@ApiModelProperty(value="产品ID",required=false)
	private Long product_inner_sn;
	
	@ApiModelProperty(value="产品名称",required=false)
	private String product_name;
	
	@ApiModelProperty(value="规格型号ID",required=false)
	private Long specification_inner_sn;
	
	@ApiModelProperty(value="规格型号名称",required=false)
	private String specification;
	
	@ApiModelProperty(value="产品类型 0-自建产品 1-厂商产品",required=false)
	private Integer product_type;
	
	@ApiModelProperty(value="注册证编号",required=false)
	private String mdrf_sn;
	
	@ApiModelProperty(value="规格型号编码",required=false)
	private String specification_vendor_sn;
	
	@ApiModelProperty(value="包装单位ID",required=false)
	private Long unit_inner_sn;
	
	@ApiModelProperty(value="包装单位名称",required=false)
	private String unit;
	
	@ApiModelProperty(value="发布时间",required=false)
	private Date publish_time;
}
