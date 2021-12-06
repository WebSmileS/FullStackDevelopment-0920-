package com.yrt.project.api.business.surgery.vo;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Deprecated
public class AddOrthopaedicIntentionForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 医院内部编号 */
	@ApiModelProperty(value="医院ID",required=true)
	@NotNull(message = "医院ID不能为空")
	private Long hospital_inner_sn;
	/** 经销商内部编号 */
	@ApiModelProperty(value="经销商ID",required=true)
	@NotNull(message = "经销商ID不能为空")
	private Long dealer_inner_sn;
	/** 开始日期 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty(value="开始日期",required=true)
	@NotNull(message = "开始日期不能为空")
	private Date begin_date;
	/** 截止日期 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty(value="截止日期",required=true)
	@NotNull(message = "截止日期不能为空")
	private Date end_date;
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description;
	
	/** 颅骨状态 */
	@ApiModelProperty(value="颅骨状态",required=true)
	@NotNull(message = "颅骨状态不能为空")
	private Integer t0_status;
	/** 脊柱状态 */
	@ApiModelProperty(value="脊柱状态",required=true)
	@NotNull(message = "脊柱状态不能为空")
	private Integer t1_status;
	/** 关节状态 */
	@ApiModelProperty(value="关节状态",required=true)
	@NotNull(message = "关节状态不能为空")
	private Integer t2_status;
	/** 创伤状态 */
	@ApiModelProperty(value="创伤状态",required=true)
	@NotNull(message = "创伤状态不能为空")
	private Integer t3_status;
}
