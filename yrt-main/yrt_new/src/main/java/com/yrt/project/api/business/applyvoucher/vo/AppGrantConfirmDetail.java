package com.yrt.project.api.business.applyvoucher.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AppGrantConfirmDetail implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="明细内部编号",required=true)
	@NotNull(message = "明细内部编号不能为空")
	private Long detail_inner_sn;
	@ApiModelProperty(value="领取人内部编号",required=true)
	@NotNull(message = "领取人内部编号不能为空")
	private Long receiver_inner_sn;
	@ApiModelProperty(value="领取人姓名",required=true)
	@NotBlank(message = "领取人姓名不能为空")
	private String receiver_name;
	@ApiModelProperty(value="领取数量",required=true)
	@NotNull(message = "领取数量不能为空")
	private BigDecimal get_quantity;
	@ApiModelProperty(value="产品类型 0:经销商(自建)产品   1:厂商产品",required=true)
	@NotNull(message = "产品类型不能为空")
	private Integer product_type;

}
