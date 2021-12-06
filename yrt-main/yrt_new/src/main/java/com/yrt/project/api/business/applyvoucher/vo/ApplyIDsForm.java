package com.yrt.project.api.business.applyvoucher.vo;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApplyIDsForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="医院ID",required=true)
	@NotNull(message = "医院不能为空")
	private Long hospital_inner_sn;
	
	@ApiModelProperty(value="单据ID",required=true)
	@NotNull(message = "单据ID不能为空")
	private Long order_id;
	
	@ApiModelProperty(value="单据明细列表[{'detail_id':单据明细ID,'product_type':产品类型 0:经销商(自建)产品   1:厂商产品}]",required=true)
	@NotNull(message = "单据ID不能为空")
	private List<DetailIdsForm> details;
}
