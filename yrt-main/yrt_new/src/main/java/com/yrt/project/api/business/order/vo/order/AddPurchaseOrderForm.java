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
public class AddPurchaseOrderForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 当前机构ID */
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long partA_id;
	
	/** 相关机构ID */
	@ApiModelProperty(value="相关机构ID",required=true)
	@NotNull(message = "相关机构ID不能为空")
	private Long partB_id;
	
	/** 采购员ID */
	@ApiModelProperty(value="采购员ID(采购订单专用)",required=true)
	@NotNull(message = "采购员ID不能为空")
	private Long buyer_id;
	
	/** 部门内部编号 */
	@ApiModelProperty(value="部门内部编号(采购订单专用)",required=true)
	@NotNull(message = "部门ID不能为空")
	private Long department_id;
	
	@ApiModelProperty(value="单据编号",required=false)
	private String po_sn;
	
	@ApiModelProperty(value="期望到货日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date expect_aog_date;
	
	@ApiModelProperty(value="状态0-草稿 1-正式 2-已审批 3-确认 4-已终止 5-已完结",required=true)
	@NotNull(message = "状态不能为空")
	private Integer status;
	
	@ApiModelProperty(value="描述",required=false)
	private String description = "";
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
	
	@ApiModelProperty(value = "收货仓库ID", required = true)
	@NotNull(message = "收货仓库ID不能为空")
	private Long warehouse_inner_sn;
	@ApiModelProperty(value = "收货仓库名称", required = true)
	@NotBlank(message = "收货仓库名称不能为空")
	private String warehouse_name;
	@ApiModelProperty(value = "收货地址")
	private String delivery_address = "";
	
	@ApiModelProperty(value="订单明细列表",required=false)
	List<OrderDetailForm> details = new ArrayList<OrderDetailForm>();
}
