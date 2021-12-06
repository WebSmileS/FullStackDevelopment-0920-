package com.yrt.project.api.business.order.vo.order;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderSearchProductForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="采购方机构ID",required=true)
	@NotNull(message = "采购方机构ID不能为空")
	private Long partA_id;
	
	@ApiModelProperty(value="销售方机构ID",required=true)
	@NotNull(message = "销售方机构ID不能为空")
	private Long partB_id;
	
	@ApiModelProperty(value="产品厂商ID(自建产品就是经销商ID)",required=false)
	private Long product_vendor_id;
	@ApiModelProperty(value="产品名称",required=false)
	private String product_name;
	@ApiModelProperty(value="产品自定义分类ID",required=false)
	private Long type_inner_sn;
	@ApiModelProperty(value="产品68分类ID",required=false)
	private Long code68_sn;
	/** 型号规格厂商自定编号 */
	@ApiModelProperty(value="型号规格厂商自定编号",required=false)
	private String specification_vendor_sn;
	@ApiModelProperty(value="型号规格",required=false)
	private String specification;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;

}
