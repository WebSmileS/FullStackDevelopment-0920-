package com.yrt.project.api.business.order.vo.purchasingRecord;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchPurchasingRecordProductForm {

	@ApiModelProperty(value = "相关机构ID", notes = "与bills_type成对出现，要不都不传，要不都传")
	private Long organization_inner_sn;
	
	@ApiModelProperty(value = "单据编号")
	private String sn;
	@ApiModelProperty(value = "产品厂商")
	private String product_vendor_name;
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	@ApiModelProperty(value = "规格型号")
	private String specification;
	
	@ApiModelProperty(value = "仓库ID")
	private Long warehouse_inner_sn;
	
	@ApiModelProperty(value="业务类型", notes = "与organization_inner_sn成对出现，要不都不传，要不都传")
	private BillsType bills_type;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
}
