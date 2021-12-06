package com.yrt.project.api.business.order.vo.order;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchPurchaseRecordForm {
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message="当前机构ID不能为空")
	private Long partA;
	
	@ApiModelProperty(value="销售方名称",required=false)
	private String partB_name;
	
	@ApiModelProperty(value="单据编号",required=false)
	private String order_sn;
	
	@ApiModelProperty(value = "厂商名称",required=false)
	private String product_vendor_name;
	
	@ApiModelProperty(value="产品名称",required=false)
	private String product_name;
	
	@ApiModelProperty(value="采购员",required=false)
	private String buyer_name;
	
	@ApiModelProperty(value = "日期区间(start)",required=true)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "日期区间必填!")
	private Date start_date;

	@ApiModelProperty(value = "日期区间(end)",required=true)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "日期区间必填!")
	private Date end_date;
	
	@ApiModelProperty(value="仓库ID")
	private Long warehouse_inner_sn;
	
	@ApiModelProperty(value="状态", notes = "0-全部 1-执行中 2-完结，默认0-全部")
	private Integer status = 0;
}
