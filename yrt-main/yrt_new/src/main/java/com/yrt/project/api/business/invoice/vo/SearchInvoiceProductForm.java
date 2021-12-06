package com.yrt.project.api.business.invoice.vo;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchInvoiceProductForm {

	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message="当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="相关机构ID")
	private Long organization_inner_sn;
	
	@ApiModelProperty(value="产品名称")
	private String name;

	@ApiModelProperty(value="厂商名称")
	private String product_vendor_name;
	
	@ApiModelProperty(value="规格型号")
	private String specification;
	
	@ApiModelProperty(value="单据类型")
	private BillsType billsType;
	
	@ApiModelProperty(value = "单据编号")
	private String sn;
	
	@ApiModelProperty(value = "单据创建日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	@ApiModelProperty(value = "单据创建日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
}
