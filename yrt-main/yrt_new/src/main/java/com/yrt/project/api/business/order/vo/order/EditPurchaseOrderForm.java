package com.yrt.project.api.business.order.vo.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditPurchaseOrderForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long partA_id;
	
	@ApiModelProperty(value="相关机构ID",required=true)
	@NotNull(message = "相关机构ID不能为空")
	private Long partB_id;
	
	@ApiModelProperty(value="订单ID",required=true)
	@NotNull(message = "订单ID不能为空")
	private Long po_so_inner_sn;
	
	@ApiModelProperty(value="采购员ID(采购订单专用)",required=false)
	private Long buyer_id;
	private Long buyer_id_old;
	@ApiModelProperty(value="采购员名称(采购订单专用)",required=false)
	private String buyer_name;
	
	@ApiModelProperty(value="部门内部编号(采购订单专用)",required=false)
	private Long department_inner_sn;
	private Long department_inner_sn_old;
	
	@ApiModelProperty(value="部门名称(采购订单专用)",required=false)
	private String department_name;
	
	@ApiModelProperty(value="期望到货日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date expect_aog_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date expect_aog_date_old;
	@ApiModelProperty(hidden = true)
	private Integer expect_date_int = 0;
	
	@ApiModelProperty(value="状态 0-草稿 1-正式 2-已审批 3-确认 4-已终止 5-已完结",required=true)
	@NotNull(message = "状态不能为空")
	private Integer status;
	
	/** 单据生成方 0-采购订单 1-销售订单 */
//	@ApiModelProperty(value="单据生成方 0-采购订单 1-销售订单",required=true)
//	@NotNull(message = "单据生成方不能为空")
//	private Integer create_organization_type;

	@ApiModelProperty(value="描述",required=false)
	private String description;
	private String description_old;
	
	@ApiModelProperty(value = "收货仓库ID",required=true)
	@NotNull(message = "收货仓库ID不能为空")
	private Long warehouse_inner_sn;
	@ApiModelProperty(value = "收货仓库名称",required=true)
	@NotBlank(message = "收货仓库名称不能为空")
	private String warehouse_name;
	@ApiModelProperty(value = "收货地址")
	private String delivery_address;
	private String delivery_address_old;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
	
	@ApiModelProperty(value="订单明细列表",required=false)
	List<OrderDetailForm> details = new ArrayList<OrderDetailForm>();
	
	@ApiModelProperty(hidden = true)
	private Map<String,Object> info = new HashMap<>();
	
	@ApiModelProperty(hidden = true)
	public Map<String,Object> getInfo() {
		if(StringUtils.isNotNull(buyer_id) || StringUtils.isNotNull(buyer_id_old)) {
			info.put("buyer_inner_sn", buyer_id);
			info.put("buyer_name", buyer_name);
		}
		if(StringUtils.isNotNull(expect_aog_date) || StringUtils.isNotNull(expect_aog_date_old)) {
			if(StringUtils.isNull(expect_aog_date)) {
				expect_date_int = 1;
			}
			info.put("expect_aog_date", expect_aog_date);
			info.put("expect_date_int", expect_date_int);
		}
		if(StringUtils.isNotNull(department_inner_sn) || StringUtils.isNotNull(department_inner_sn_old)) {
			if(StringUtils.isNull(department_inner_sn)) {
				department_inner_sn = -1L;
				department_name = null;
			}
			info.put("purchase_department_inner_sn", department_inner_sn);
			info.put("purchase_department", department_name);
		}
		if(StringUtils.isNotNull(warehouse_inner_sn) && StringUtils.isNotEmpty(warehouse_name)) {
			info.put("warehouse_inner_sn", warehouse_inner_sn);
			info.put("warehouse_name", warehouse_name);
		}else {
			throw new ValidateException("收货仓库不能为空!");
		}
		if((StringUtils.isNotEmpty(delivery_address) || StringUtils.isNotEmpty(delivery_address_old))) {
			info.put("delivery_address", delivery_address);
		}
		if((StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(description_old))) {
			info.put("description", description);
		}
		info.put("partA", partA_id);
		info.put("partB", partB_id);
		info.put("po_so_inner_sn", po_so_inner_sn);
		info.put("status", status);
		return info;
	}
}
