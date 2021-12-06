package com.yrt.project.api.business.finance.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.ReceiveStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchReceiveForm {

	@ApiModelProperty(value = "相关机构ID")
	private Long organization_inner_sn;
	@ApiModelProperty(value = "收款编号")
	private String receive_sn;
	@ApiModelProperty(value = "收款状态 默认ALL-全部， ALL-全部 TO_BE_COLLECTED-待收款 RECEIVED-已收款 RECORDED-已入账")
	private ReceiveStatus receive_status = ReceiveStatus.ALL;
	@ApiModelProperty(value = "是否作废 默认0，0-未作废 1-作废")
	private int isCancel = 0;
	
	@ApiModelProperty(value="单据类型")
	private BillsType billsType;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
}
