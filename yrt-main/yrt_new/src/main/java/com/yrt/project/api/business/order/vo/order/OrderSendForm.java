package com.yrt.project.api.business.order.vo.order;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderSendForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="相关机构ID",required=true)
	@NotNull(message = "相关机构ID不能为空")
	private Long organization_inner_sn;
	
	@ApiModelProperty(value="销售订单ID",required=true)
	@NotNull(message = "销售订单不能为空")
	private Long po_so_inner_sn;
	
	@ApiModelProperty(value="部门ID",required=true)
	@NotNull(message = "部门ID不能为空")
	private Long department_inner_sn;
	
	@ApiModelProperty(value="部门名称",required=true)
	@NotBlank(message = "部门名称不能为空")
	private String department_name;
	
	@ApiModelProperty(value="单据编号",required=false)
	private String po_sn;
	
	@ApiModelProperty(value="摘要",required=false)
	private String description = "";
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
	
	@ApiModelProperty(value="单据明细列表[\r\n" + 
			"        {\r\n" + 
			"            \"detail_inner_sn\":\"销售订单明细ID\",\r\n" + 
			"            \"product_vendor_inner_sn\":\"产品厂商ID\",\r\n" + 
			"            \"product_vendor_name\":\"产品厂商名称\",\r\n" + 
			"            \"product_inner_sn\":\"产品ID不能为空\",\r\n" + 
			"            \"product_name\":\"产品名称\",\r\n" + 
			"            \"specification_inner_sn\":\"型号规格ID\",\r\n" + 
			"            \"specification\":\"型号规格名称\",\r\n" + 
			"            \"unit_inner_sn\":\"单位ID\",\r\n" + 
			"            \"unit\":\"包装单位名称\",\r\n" + 
			"            \"quantity\":\"数量\",\r\n" + 
			"            \"sort_number\":\"排序号\",\r\n" + 
			"            \"product_type\":\"产品类型 0:经销商(自建)产品 1:厂商产品\"\r\n" + 
			"        }\r\n" + 
			"    ]",required=true)
	@NotNull(message = "单据ID不能为空")
	private List<SendDetailForm> details;
}
