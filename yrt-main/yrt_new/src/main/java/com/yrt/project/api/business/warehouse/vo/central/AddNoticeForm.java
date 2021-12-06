package com.yrt.project.api.business.warehouse.vo.central;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddNoticeForm {

	@ApiModelProperty(value="租户ID",required = true)
	@NotNull(message = "租户ID不能为空")
	private Long dealer_inner_sn;
	@ApiModelProperty(value="租户名称",required = true)
	@NotBlank(message = "租户名称不能为空")
	private String dealer_name;
	@ApiModelProperty(value="采购/销售公司名称",required = true)
	@NotBlank(message = "采购/销售公司名称不能为空")
	private String company_name;
	@ApiModelProperty(value="入库单/出库单ID,新增不用填")
	private Long record_inner_sn;
	@ApiModelProperty(value="单据编号,新增不用填")
	private String sn = "";
	private String warehouse_name;
	@ApiModelProperty(value="单据类型,新增不用填")
	private BillsType bills_type = null;
	@ApiModelProperty(value="状态 0-保存 1-收货/发货")
	private Integer status = 0;
	
	@ApiModelProperty(value="产品明细列表")
	private List<NoticeDetailForm> details;
}
