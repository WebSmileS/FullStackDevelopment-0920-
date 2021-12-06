package com.yrt.project.modular.warehouse.domain;

import java.util.Date;
import java.util.List;

import com.yrt.common.dict.LogisticsMode;
import com.yrt.common.dict.LogisticsStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OdoLogisticsRecord {

	@ApiModelProperty(value="物流信息ID")
	private Long id;
	@ApiModelProperty(value="机构ID")
	private Long org_id;
	@ApiModelProperty(value="物流方式")
	private LogisticsMode logistics_mode;
	@ApiModelProperty(value="第三方物流公司")
	private String third_logistics_company;
	@ApiModelProperty(value="物流单号")
	private String logistics_sn;
	@ApiModelProperty(value="物流信息状态")
	private LogisticsStatus status;
	@ApiModelProperty(value="创建人")
	private String created_by;
	@ApiModelProperty(value="创建时间")
	private Date created_time;
	@ApiModelProperty(value="确定人")
	private String confirm_by;
	@ApiModelProperty(value="确定时间")
	private Date confirm_time;
	
	@ApiModelProperty(value="物流信息明细")
	private List<OdoNotice> details;
}
