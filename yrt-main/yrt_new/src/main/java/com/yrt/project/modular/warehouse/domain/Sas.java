package com.yrt.project.modular.warehouse.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Sas implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "机构内部编号")
	private Long org_id;
	@ApiModelProperty(value = "仓库内部编号")
	private Long warehouse_inner_sn;
	@ApiModelProperty(value = "储位分配单内部编号")
	private Long sas_inner_sn;
	@ApiModelProperty(value = "制单人内部编号")
	private Long making_people_inner_sn;
	@ApiModelProperty(value = "作业人内部编号")
	private Long operator_inner_sn;
	@ApiModelProperty(value = "状态")
	private Integer status;;
	@ApiModelProperty(value = "制单时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date create_time;
	@ApiModelProperty(value = "储位分配单编号")
	private String sas_sn;
	@ApiModelProperty(value = "描述")
	private String description;
	@ApiModelProperty(value = "仓库名称")
	private String warehouse_name;
	@ApiModelProperty(value = "制单人姓名")
	private String making_people_name;
	@ApiModelProperty(value = "作业人姓名")
	private String operator_name;
	@ApiModelProperty(value = "入库单内部编号")
	private Long grn_inner_sn;
	@ApiModelProperty(value="单据类型",required=true)
	private BillsType billsType;
	
	private List<SasDetail> details = new ArrayList<>();
}
