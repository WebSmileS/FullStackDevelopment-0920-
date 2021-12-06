package com.yrt.project.modular.warning.domain;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.SystemType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductMdrfWarning implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "唯一ID")
	private Long id;
	@ApiModelProperty(value = "创建预警信息机构唯一ID(平台用0)")
	private Long created_org_id;
	@ApiModelProperty(value = "创建预警信息机构类型(PLATFORM-平台 HOSPITAL-医院 DEALER-经销商 VENDOR-厂商)")
	private SystemType created_system_type;
	
	@ApiModelProperty(value = "厂商ID")
	private Long vendor_inner_sn;
	@ApiModelProperty(value = "厂商名称")
	private String vendor_name;
	@ApiModelProperty(value = "产品ID")
	private Long product_inner_sn;
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	@ApiModelProperty(value = "注册证ID")
	private Long mdrf_inner_sn;
	@ApiModelProperty(value = "注册证号")
	private String register_sn;
	@ApiModelProperty(value = "过期日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date expiration_date;
	
	@ApiModelProperty(value = "发证部门")
	private String issue_organization;
}
