package com.yrt.project.api.business.warehouse.vo.receipt;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchArrivalRecordForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "收货单编号")
	private String sn;
	
	@ApiModelProperty(value = "相关机构ID")
	private Long organization_inner_sn;
	
	@ApiModelProperty(value = "业务类型,相关机构ID不为空时bills_type必填")
	private BillsType bills_type;
	
	@ApiModelProperty(value = "送货人姓名")
	private String deliverymen_name;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty(value = "起始时间")
	private Date begin_date;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty(value = "结束时间")
	private Date end_date;
	
	@ApiModelProperty(value = "是否历史")
	private boolean history = false;

	@ApiModelProperty(value = "是否质管")
	private boolean qualityControl = false;
}
