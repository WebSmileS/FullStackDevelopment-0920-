package com.yrt.project.modular.warehouse.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.InspectionStatus;
import com.yrt.framework.aspectj.lang.annotation.Excel;
import com.yrt.framework.aspectj.lang.annotation.Excel.Type;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 验货单
 * @author Runner
 *
 */
@Data
public class InspectionRecord {

	@ApiModelProperty(value="收货单ID")
	private Long id;
	@ApiModelProperty(value="机构ID")
	private Long org_id;
	@ApiModelProperty(value="机构类型")
	private Integer system_type;
	@ApiModelProperty(value="业务类型")
	private BillsType bills_type;
	@ApiModelProperty(value="单据编号")
	@Excel(name = "单据编号")
	private String sn;
	private Long organization_inner_sn;
	@Excel(name = "供货单位")
	private String organization_name;
	
	private Long warehouse_inner_sn;
	@Excel(name = "仓库名称")
	private String warehouse_name;
	
	@Excel(name = "状态", readConverterExp = "0=未验货,1=已验货,2=已复核")
	@ApiModelProperty(value = "状态 0-未验货 1-已验货 2-已复核")
	private InspectionStatus inspection_status;
	
	@Excel(name = "是否作废", readConverterExp = "0=未作废,1=作废", type = Type.EXPORT)
	private Integer is_cancel;
	@Excel(name = "摘要", type = Type.EXPORT)
	private String description;
	@Excel(name = "作废原因", type = Type.EXPORT)
	private String cancel_reason;
	
	private String created_by;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date created_time;
	private String inspection_by;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date inspection_time;
	private String confirm_by;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date confirm_time;
	private String update_by;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date update_time;
	private String cancel_by;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date cancel_time;
	
	private List<InspectionRecordDetail> details;
}
