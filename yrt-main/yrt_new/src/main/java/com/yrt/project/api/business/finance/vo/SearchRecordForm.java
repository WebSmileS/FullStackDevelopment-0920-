package com.yrt.project.api.business.finance.vo;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchRecordForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "当前机构ID", required = true)
	@NotNull(message = "当前机构ID不能空")
	private Long org_id;
	
	@ApiModelProperty(value = "仓库ID")
	private Long warehouse_model_inner_sn;
	@ApiModelProperty(value = "单据编号")
	private String sn;
	@ApiModelProperty(value = "经手人名称")
	private String handler_name;
	@ApiModelProperty(value = "相关机构名称")
	private String organization_name;
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	@ApiModelProperty(value = "产品生产商ID")
	private String product_vendor_inner_sn;
	@ApiModelProperty(value = "经销商产品生产商ID")
	private String product_dealer_inner_sn;
	@ApiModelProperty(value = "部门ID")
	private String department_inner_sn;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
}
