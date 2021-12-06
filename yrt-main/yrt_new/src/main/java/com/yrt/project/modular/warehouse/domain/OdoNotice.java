package com.yrt.project.modular.warehouse.domain;

import com.yrt.common.dict.OdoNoticeStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class OdoNotice extends Notice {

	@ApiModelProperty(value="采购公司名称")
	private String purchasing_company_name;
	@ApiModelProperty(value="出库单ID")
	private Long odo_inner_sn;
	
	@ApiModelProperty(value="出库通知状态")
	private OdoNoticeStatus status;
	
}
