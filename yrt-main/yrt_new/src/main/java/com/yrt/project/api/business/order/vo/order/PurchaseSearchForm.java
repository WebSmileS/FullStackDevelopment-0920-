package com.yrt.project.api.business.order.vo.order;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PurchaseSearchForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message="当前机构ID不能为空")
	private Long partA_id;
	
	@ApiModelProperty(value="销售方名称",required=false)
	private String partB_name;
	
	@ApiModelProperty(value="单据编号",required=false)
	private String order_sn;
	
	@ApiModelProperty(value="采购员ID",required=false)
	private Long buyer_id;
	
	@ApiModelProperty(value="部门ID",required=false)
	private Long department_id;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;

	@ApiModelProperty(value = "日期区间(start)")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date start_date;

	@ApiModelProperty(value = "日期区间(end)")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
}
