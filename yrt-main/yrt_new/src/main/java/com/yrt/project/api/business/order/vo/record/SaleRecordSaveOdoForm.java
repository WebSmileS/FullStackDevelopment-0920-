package com.yrt.project.api.business.order.vo.record;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SaleRecordSaveOdoForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 相关机构ID */
	@ApiModelProperty(value="相关机构ID",required=true)
	@NotNull(message = "相关机构ID不能为空")
	private Long organization_inner_sn;
	@ApiModelProperty(value="相关机构名称",required=true)
	@NotBlank(message = "相关机构名称不能为空")
	private String organization_name;
	/** 仓库内部编号 */
	@ApiModelProperty(value="仓库ID",required=true)
	@NotNull(message = "仓库不能为空")
	private Long warehouse_inner_sn;
	@ApiModelProperty(value="仓库名称",required=true)
	@NotBlank(message = "仓库名称不能为空")
	private String warehouse_name;
	/** 经手人内部编号 */
	@ApiModelProperty(value="经手人ID",required=true)
	@NotNull(message = "经手人ID不能为空")
	private Long handler_inner_sn;
	@ApiModelProperty(value="经手人名称",required=true)
	@NotBlank(message = "经手人名称不能为空")
	private String handler_name;
	/** 仓库人员内部编号 */
	@ApiModelProperty(value="仓库人员ID",required=false)
	private Long warehouse_operator_inner_sn;
	@ApiModelProperty(value="仓库人员名称",required=false)
	private String warehouse_operator_name = "";
	/** 类型内部编号 */
	@ApiModelProperty(value="出库单类型ID",required=true)
	@NotNull(message = "出库单类型ID不能为空")
	private Long type_inner_sn;
	@ApiModelProperty(value="出库单类型名称",required=true)
	@NotBlank(message = "出库单类型名称不能为空")
	private String type_name;
	/** 开单部门内部编号 */
	@ApiModelProperty(value="开单部门ID",required=true)
	@NotNull(message = "开单部门ID不能为空")
	private Long department_inner_sn;
	@ApiModelProperty(value="开单部门名称",required=true)
	@NotBlank(message = "开单部门名称不能为空")
	private String department_name = "";
	/** 状态 */
	@ApiModelProperty(value="状态 0-正式 1-已审核 2-已出库 3-已出库复核",required=true)
	@NotNull(message = "状态不能为空")
	private Integer status;
	/** 出库单编号 */
	@ApiModelProperty(value="单据编号",required=false)
	private String odo_sn;
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description = "";
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
	
	@ApiModelProperty(value="出库单明细",required=false)
	private List<SaleRecordSaveOdoDetailForm> details = new ArrayList<>();
}
