package com.yrt.project.api.business.warehouse.vo.sas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddSasForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 当前机构ID */
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	/** 仓库内部编号 */
	@ApiModelProperty(value="仓库ID",required=true)
	@NotNull(message = "仓库不能为空")
	private Long warehouse_inner_sn;
	@ApiModelProperty(value="仓库名称",required=true)
	@NotBlank(message = "仓库名称不能为空")
	private String warehouse_name;
	/** 经手人内部编号 */
	@ApiModelProperty(value="作业人ID",required=true)
	@NotNull(message = "作业人ID不能为空")
	private Long operator_inner_sn;
	@ApiModelProperty(value="作业人名称",required=true)
	@NotBlank(message = "作业人名称不能为空")
	private String operator_name;
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description = "";
	
	@ApiModelProperty(value="储位分配单明细",required=false)
	private List<SasDetailForm> details = new ArrayList<>();
}
