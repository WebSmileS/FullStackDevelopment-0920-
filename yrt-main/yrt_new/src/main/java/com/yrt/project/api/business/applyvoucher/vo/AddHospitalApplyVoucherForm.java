package com.yrt.project.api.business.applyvoucher.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddHospitalApplyVoucherForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 医院内部编号 */
	@ApiModelProperty(value="医院ID",required=true)
	@NotNull(message = "医院ID不能为空")
	private Long hospital_inner_sn;
	/** 单据编号 */
	@ApiModelProperty(value="单据编号",required=false)
	private String voucher_sn;
	/** 申领人 */
//	@ApiModelProperty(value="申领人ID",required=true)
//	@NotNull(message = "申领人不能为空")
//	private Long proposer_inner_sn;
	/** 部门内部编号 */
	@ApiModelProperty(value="部门ID",required=true)
	@NotNull(message = "部门不能为空")
	private Long department_inner_sn;
	/** 期望发放日期(之前) */
	@ApiModelProperty(value="期望发放日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date expect_date;
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description = "";
	/** 部门名称 */
	@ApiModelProperty(value="部门名称",required=false)
	private String department_name = "";
	/** 状态 */
	@ApiModelProperty(value="状态 0-草稿 1-申领",required=true)
	private Integer status;
	@ApiModelProperty(value="产品列表",required=true)
	@NotNull(message = "产品列表不能为空")
	private List<ApplyVoucherDetailForm> details;

	@ApiModelProperty(value="仓库内部编号",notes = "可为空")
	private Long warehouse_inner_sn;

	@ApiModelProperty(value="仓库名称",notes = "可为空")
	private String warehouse_inner_sn_name;
}
