package com.yrt.project.api.business.warehouse.vo.sas;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SasIDForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="仓库ID",required=true)
	@NotNull(message = "仓库ID不能为空")
	private Long warehouse_inner_sn;
	
	@ApiModelProperty(value="储位分配单ID",required=true)
	@NotNull(message = "储位分配单ID不能为空")
	private Long sas_inner_sn;
	
}
