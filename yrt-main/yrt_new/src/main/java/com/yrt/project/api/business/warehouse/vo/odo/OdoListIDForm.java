package com.yrt.project.api.business.warehouse.vo.odo;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OdoListIDForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 当前机构ID */
	@ApiModelProperty(value = "当前机构ID", required = true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;

	@ApiModelProperty(value = "出库单ID列表", required = true)
	@NotNull(message = "出库单ID列表不能为空")
	private List<OdoIds> ids;

}
