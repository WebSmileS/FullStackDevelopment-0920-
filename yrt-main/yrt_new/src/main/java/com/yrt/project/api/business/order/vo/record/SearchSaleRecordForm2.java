package com.yrt.project.api.business.order.vo.record;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchSaleRecordForm2 implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="状态 ( 1-正式 2-已审核",required=false)
	private Integer status;
	
	@ApiModelProperty(value="相关机构名称",required=false)
	private String organization_name;
	
	@ApiModelProperty(value="销售单编号",required=false)
	private String sl_sn;
	
	@ApiModelProperty(value="销售人员名称",required=false)
	private String seller_name;
	
}
