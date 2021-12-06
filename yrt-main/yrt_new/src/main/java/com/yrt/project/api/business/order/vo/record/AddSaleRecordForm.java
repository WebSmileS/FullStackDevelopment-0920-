package com.yrt.project.api.business.order.vo.record;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddSaleRecordForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="相关机构ID",required=true)
	@NotNull(message = "相关机构ID不能为空")
	private Long organization_inner_sn;
	@ApiModelProperty(value="相关机构名称",required=true)
	@NotBlank(message = "相关机构名称不能为空")
	private String organization_name;
	
	@ApiModelProperty(value="销售员ID",required=true)
	@NotNull(message = "销售员ID不能为空")
	private Long seller_inner_sn;
	@ApiModelProperty(value="销售员名称",required=true)
	@NotBlank(message = "销售员名称不能为空")
	private String seller_name;
	
	@ApiModelProperty(value="部门内部编号",required=true)
	@NotNull(message = "部门ID不能为空")
	private Long department_id;
	@ApiModelProperty(value="部门名称",required=true)
	@NotBlank(message = "部门名称不能为空")
	private String department_name;
	
	@ApiModelProperty(value="收款日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date collection_date;
	
	@ApiModelProperty(value="采购方联系人",required=false)
	private String purchaser_linkman = "";
	@ApiModelProperty(value="采购方联系电话",required=false)
	private String purchaser_phone = "";
	@ApiModelProperty(value="销售方联系人",required=false)
	private String seller_linkman = "";
	@ApiModelProperty(value="销售方联系电话",required=false)
	private String seller_phone = "";
	@ApiModelProperty(value="描述",required=false)
	private String description = "";
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
	
	@ApiModelProperty(value="销售单明细列表",required=false)
	List<SaleRecordDetailForm> details = new ArrayList<SaleRecordDetailForm>();
}
