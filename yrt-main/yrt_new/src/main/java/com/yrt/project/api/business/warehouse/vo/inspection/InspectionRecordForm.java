package com.yrt.project.api.business.warehouse.vo.inspection;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.InspectionStatus;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InspectionRecordForm implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "验货单ID,编辑时不能为空")
	private Long id;
	
	@ApiModelProperty(value = "相关机构内部编号", required = true)
	@NotNull(message = "验货单内部编号不能为空")
	private Long organization_inner_sn;
	@ApiModelProperty(value = "相关机构名称", required = true)
	@NotBlank(message = "相关机构名称不能为空")
	private String organization_name;
	
	@ApiModelProperty(value = "仓库ID", required = true)
	@NotNull(message = "仓库ID不能为空")
	private Long warehouse_inner_sn;
	@ApiModelProperty(value = "仓库", required = true)
	@NotBlank(message = "仓库不能为空")
	private String warehouse_name = "";
	
	@ApiModelProperty(value = "状态", required = true)
	@NotNull(message = "状态不能为空")
	private InspectionStatus inspection_status;
	
	@ApiModelProperty(value = "描述")
	private String description;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType bills_type;
	
	@ApiModelProperty(value = "验货单明细", required = true)
	@NotNull(message = "验货单明细不能为空")
	private List<InspectionRecordDetailForm> details;

}
