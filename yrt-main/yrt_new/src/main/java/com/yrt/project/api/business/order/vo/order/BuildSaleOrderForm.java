package com.yrt.project.api.business.order.vo.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BuildSaleOrderForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="相关机构ID",required=true)
	@NotNull(message = "相关机构ID不能为空")
	private Long organization_inner_sn;
	@ApiModelProperty(value="相关机构名称",required=true)
	@NotBlank(message = "相关机构名称不能为空")
	private String organization_name;
	
	@ApiModelProperty(value="订单内部ID",required=true)
	@NotNull(message = "订单内部ID不能为空")
	private Long po_so_inner_sn;
	@ApiModelProperty(value="仓库内部ID",required=false)
	private Long warehouse_inner_sn;
	
	@ApiModelProperty(value="销售员ID",required=true)
	@NotNull(message = "销售员ID不能为空")
	private Long seller_inner_sn;
	@ApiModelProperty(value="销售员名称",required=true)
	@NotBlank(message = "销售员名称不能为空")
	private String seller_name;
	
	@ApiModelProperty(value="审核人ID",required=false)
	private Long auditor_inner_sn = null;
	@ApiModelProperty(value="审核人名称",required=false)
	private String auditor_name = "";
	
	@ApiModelProperty(value="部门内部编号",required=true)
	@NotNull(message = "部门ID不能为空")
	private Long department_id;
	@ApiModelProperty(value="部门名称",required=true)
	@NotBlank(message = "部门名称不能为空")
	private String department_name;
	
//	@ApiModelProperty(value="送货日期",required=false)
//	@JsonFormat(pattern = "yyyy-MM-dd")
//	private Date delivery_date;
	@ApiModelProperty(value="收款日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date collection_date;
	
	@ApiModelProperty(value="销售单编号",required=false)
	private String sl_sn;
	@ApiModelProperty(value="状态 0-草稿 1-正式",required=true)
	@NotNull(message = "状态不能为空")
	private Integer status;
	
	@ApiModelProperty(value="采购方联系人",required=false)
	private String purchaser_linkman = "";
	@ApiModelProperty(value="采购方联系电话",required=false)
	private String purchaser_phone = "";
	@ApiModelProperty(value="销售方联系人",required=false)
	private String seller_linkman = "";
	@ApiModelProperty(value="销售方联系电话",required=false)
	private String seller_phone = "";
	@ApiModelProperty(value="描述",required=false)
	private String description = "";
//	@ApiModelProperty(value="送(收)货地址",required=false)
//	private String delivery_address = "";
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
	
	@ApiModelProperty(value="订单明细列表",required=false)
	List<BuildSaleOrderDetailForm> details = new ArrayList<BuildSaleOrderDetailForm>();
}
