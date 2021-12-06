package com.yrt.project.api.business.warehouse.vo.central;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.yrt.common.dict.LogisticsMode;
import com.yrt.common.dict.LogisticsStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LogisticsForm {

	@ApiModelProperty(value="物流信息ID,只有编辑时填")
	private Long id;
	@ApiModelProperty(value="物流方式(0-自送 1-第三方物流公司)", required = true)
	private LogisticsMode logistics_mode = LogisticsMode.SELF;
	@ApiModelProperty(value="第三方物流公司")
	private String third_logistics_company;
	@ApiModelProperty(value="第三方物流单号")
	private String logistics_sn;
	
	@ApiModelProperty(value="物流信息状态,默认草稿状态")
	private LogisticsStatus status = LogisticsStatus.DRAFTD;
	
	@ApiModelProperty(value="通知明细ID列表", required = true)
	@NotEmpty(message = "通知明细ID列表不能为空")
	private List<Long> detail;
}
