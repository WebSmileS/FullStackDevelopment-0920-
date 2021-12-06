package com.yrt.project.api.business.warehouse.vo.central;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.LogisticsMode;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchLogisticsForm {

	@ApiModelProperty(value="物流类型, 0-自送 1-第三方物流公司")
	private LogisticsMode logistics_mode;
	
	@ApiModelProperty(value="物流单号")
	private String logistics_sn;
	
	@ApiModelProperty(value="开始日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	
	@ApiModelProperty(value="截止日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
	
	@ApiModelProperty(value="是否历史列表,默认false",required=false)
	Boolean isHistory = false;
}
