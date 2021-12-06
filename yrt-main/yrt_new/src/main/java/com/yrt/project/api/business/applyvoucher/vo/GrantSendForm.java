package com.yrt.project.api.business.applyvoucher.vo;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GrantSendForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="医院ID",required=true)
	@NotNull(message = "医院ID不能为空")
	private Long hospital_inner_sn;
	
	@ApiModelProperty(value="申领单ID",required=true)
	@NotNull(message = "申领单ID不能为空")
	private Long av_inner_sn;
	
	@ApiModelProperty(value="部门ID",required=false)
//	@NotNull(message = "部门ID不能为空")
	private Long department_inner_sn;
	
	@ApiModelProperty(value="部门名称",required=false)
//	@NotBlank(message = "部门名称不能为空")
	private String department_name;
	
	@ApiModelProperty(value = "仓库ID", required = true)
	@NotNull(message = "仓库ID不能为空")
	private Long warehouse_inner_sn;
	
	@ApiModelProperty(value = "仓库名称", required = true)
	@NotBlank(message = "仓库名称不能为空")
	private String warehouse_name;
	
	@ApiModelProperty(value="状态 1-正式 2-已审核",required=false)
	private Integer status = 1;
	
	@ApiModelProperty(value="摘要",required=false)
	private String description = "";
	
	@ApiModelProperty(value="单据明细列表[\r\n" + 
			"        {\r\n" + 
			"            \"product_vendor_inner_sn\":\"产品厂商ID\",\r\n" + 
			"            \"product_vendor_name\":\"产品厂商名称\",\r\n" + 
			"            \"product_inner_sn\":\"产品ID不能为空\",\r\n" + 
			"            \"product_name\":\"产品名称\",\r\n" + 
			"            \"specification_inner_sn\":\"型号规格ID\",\r\n" + 
			"            \"specification\":\"型号规格名称\",\r\n" + 
			"            \"unit_inner_sn\":\"单位ID\",\r\n" + 
			"            \"unit\":\"包装单位名称\",\r\n" + 
			"            \"grant_quantity\":\"批准发放量\",\r\n" + 
			"            \"sort_number\":\"排序号\",\r\n" + 
			"            \"need_quantity\":\"还需发放量\",\r\n" + 
			"            \"min_unit_grant_quantity\":\"最小单位转换量\",\r\n" + 
			"            \"product_type\":\"产品类型 0:经销商(自建)产品 1:厂商产品\"\r\n" + 
			"        }\r\n" + 
			"    ]",required=true)
	@NotNull(message = "单据ID不能为空")
	private List<GrantSendDetailForm> details;
}
