package com.yrt.project.api.business.order.vo.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddSaleOrderForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 相关机构ID */
	@ApiModelProperty(value="相关机构ID",required=true)
	@NotNull(message = "相关机构ID不能为空")
	private Long partA_id;
	
	/** 当前机构ID */
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long partB_id;
	
	@ApiModelProperty(value="单据编号",required=false)
	private String po_sn;
	
	@ApiModelProperty(value="期望到货日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date expect_aog_date;
	
	@ApiModelProperty(value="状态0-草稿 1-正式 2-已审批 3-确认 4-已终止 5-已完结",required=true)
	@NotNull(message = "状态不能为空")
	private Integer status;
	
	/** 单据生成方 0-采购订单 1-销售订单 */
//	@ApiModelProperty(value="单据生成方 0-采购订单 1-销售订单",required=true)
//	@NotNull(message = "相关机构ID不能为空")
//	private Integer create_organization_type;

	@ApiModelProperty(value="描述",required=false)
	private String description = "";
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
	
	@ApiModelProperty(value="订单明细列表",required=false)
	List<OrderDetailForm> details = new ArrayList<OrderDetailForm>();
}
