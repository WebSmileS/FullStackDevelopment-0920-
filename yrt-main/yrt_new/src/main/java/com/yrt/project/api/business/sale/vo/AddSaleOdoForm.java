package com.yrt.project.api.business.sale.vo;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;
import com.yrt.project.api.business.warehouse.vo.odo.OdoDetailForm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddSaleOdoForm {
	
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	@ApiModelProperty(value="相关机构ID",required=true)
	@NotNull(message = "相关机构ID不能为空")
	private Long organization_inner_sn;
	@ApiModelProperty(value="相关机构名称",required=true)
	@NotBlank(message = "相关机构名称不能为空")
	private String organization_name;
	
	@ApiModelProperty(value="仓库ID",required=true)
	@NotNull(message = "仓库不能为空")
	private Long warehouse_inner_sn;
	@ApiModelProperty(value="仓库名称",required=true)
	@NotBlank(message = "仓库名称不能为空")
	private String warehouse_name;
	
	@ApiModelProperty(value="经手人ID",required=true)
	@NotNull(message = "经手人ID不能为空")
	private Long handler_inner_sn;
	@ApiModelProperty(value="经手人名称",required=true)
	@NotBlank(message = "经手人名称不能为空")
	private String handler_name;

	@ApiModelProperty(value="仓库人员ID",required=false)
	private Long warehouse_operator_inner_sn;
	@ApiModelProperty(value="仓库人员名称",required=false)
	private String warehouse_operator_name = "";
	
	@ApiModelProperty(value="开单部门ID",required=true)
	private Long department_inner_sn;
	@ApiModelProperty(value="开单部门名称",required=true)
	private String department_name = "";
	@ApiModelProperty(value="使用科室")
	private String use_department = "";
	
	@ApiModelProperty(value="状态 0-正式 1-已审核 2-已出库",required=true)
	@NotNull(message = "状态不能为空")
	private Integer status;
	@ApiModelProperty(value="单据编号",required=false)
	private String odo_sn;
	@ApiModelProperty(value="描述",required=false)
	private String description = "";
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
	
	@ApiModelProperty(value="出库单明细",required=false)
	private List<OdoDetailForm> details = new ArrayList<>();
}
