package com.yrt.project.api.business.warehouse.vo.central;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SelectNoticeForm {

	@ApiModelProperty(value="通知ID",required = true)
	@NotNull(message = "通知ID不能为空")
	private Long id;
	@ApiModelProperty(value="通知状态 1-已收货/已收货 2-作废")
	@NotNull(message = "通知状态不能为空")
	private Integer status;
}
