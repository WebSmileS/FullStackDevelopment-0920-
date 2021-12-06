package com.yrt.project.api.business.warehouse.vo.central;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchNoticeForm {

	@ApiModelProperty(value="租户名称",required=false)
	private String dealer_name;
	
	@ApiModelProperty(value="采购/销售公司名称",required=false)
	private String company_name;
	
	@ApiModelProperty(value="产品名称",required=false)
	private String product_name;
	
	@ApiModelProperty(value="开始日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	
	@ApiModelProperty(value="截止日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
	
	@ApiModelProperty(value="是否历史列表,默认false",required=false)
	Boolean isHistory = false;
}
