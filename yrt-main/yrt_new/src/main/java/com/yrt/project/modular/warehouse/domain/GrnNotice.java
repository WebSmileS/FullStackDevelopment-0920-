package com.yrt.project.modular.warehouse.domain;

import com.yrt.common.dict.GrnNoticeStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class GrnNotice extends Notice {

	@ApiModelProperty(value="销售公司名称")
	private String sales_company_name;
	@ApiModelProperty(value="入库单ID")
	private Long grn_inner_sn;
	
	@ApiModelProperty(value="入库通知状态")
	private GrnNoticeStatus status;
}
