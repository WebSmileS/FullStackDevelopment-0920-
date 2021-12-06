package com.yrt.project.modular.warehouse.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.RefVoucherType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Grn implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "机构内部编号")
	private Long org_id;
	@ApiModelProperty(value = "机构名称")
	private String org_name;
	@ApiModelProperty(value = "入库单内部编号")
	private Long grn_inner_sn;
	@ApiModelProperty(value = "相关机构内部编号")
	private Long organization_inner_sn;
	@ApiModelProperty(value = "相关机构名称")
	private String organization_name;
	@ApiModelProperty(value = "审核人内部编号")
	private Long auditor_inner_sn;
	@ApiModelProperty(value = "审核人名称")
	private String auditor_name;
	@ApiModelProperty(value = "仓库内部编号")
	private Long warehouse_inner_sn;
	@ApiModelProperty(value = "仓库名称")
	private String warehouse_name;
	@ApiModelProperty(value = "经手人内部编号")
	private Long handler_inner_sn;
	@ApiModelProperty(value = "经手人名称")
	private String handler_name;
	@ApiModelProperty(value = "制单人内部编号")
	private Long making_people_inner_sn;
	@ApiModelProperty(value = "制单人名称")
	private String making_people_name;
	@ApiModelProperty(value = "仓库人员内部编号")
	private Long warehouse_operator_inner_sn;
	@ApiModelProperty(value = "仓库人员名称")
	private String warehouse_operator_name;
	@ApiModelProperty(value = "类型内部编号")
	private Long type_inner_sn;
	@ApiModelProperty(value = "类型名称")
	private String type_name;
	@ApiModelProperty(value = "开单部门内部编号")
	private Long department_inner_sn;
	@ApiModelProperty(value = "开单部门名称")
	private String department_name;
	@ApiModelProperty(value = "状态")
	private Integer status;
	@ApiModelProperty(value = "入库时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date in_time;
	@ApiModelProperty(value = "入库单编号")
	private String grn_sn;
	@ApiModelProperty(value = "描述")
	private String description;
	@ApiModelProperty(value="单据类型",required=true)
	private BillsType billsType;
	@ApiModelProperty(value = "相关机构类型")
	private Integer organization_type;
	@ApiModelProperty(value = "关联单据类型")
	private RefVoucherType voucher_type_c = RefVoucherType.NONE;
	@ApiModelProperty(value = "关联单据内部编号")
	private Long voucher_inner_sn_c;
	@ApiModelProperty(value = "单据生成时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date create_time;
	@ApiModelProperty(value = "采购员")
	private String buyer_name;
	@ApiModelProperty(value="使用科室，医院端的入库单才有此字段")
	private String use_department;
	@ApiModelProperty(value="签收人，医院端的入库单才有此字段")
	private String receiving_name;
	
	private List<GrnDetail> details;

}
