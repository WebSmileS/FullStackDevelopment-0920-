package com.yrt.project.api.business.applyvoucher.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditHospitalApplyVoucherForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="仓库内部编号",notes = "可为空")
	private Long warehouse_inner_sn;

	@ApiModelProperty(value="仓库名称",notes = "可为空")
	private String warehouse_inner_sn_name;

	/** 医院内部编号 */
	@ApiModelProperty(value="医院",required=true)
	@NotNull(message = "医院不能为空")
	private Long hospital_inner_sn;
	/** 单据内部编号 */
	@ApiModelProperty(value="申领单ID",required=true)
	@NotNull(message = "单据不能为空")
	private Long voucher_inner_sn;
	/** 单据编号 */
	@ApiModelProperty(value="单据编号",required=false)
	private String voucher_sn;
	private String voucher_sn_old;
	/** 申领人 */
//	@ApiModelProperty(value="申领人ID",required=false)
//	private Long proposer_inner_sn;
//	private Long proposer_inner_sn_old;
	/** 部门内部编号 */
	@ApiModelProperty(value="部门ID",required=false)
	private Long department_inner_sn;
	private Long department_inner_sn_old;
	/** 部门名称 */
	@ApiModelProperty(value="部门名称",required=false)
	private String department_name;
	private String department_name_old;
	/** 期望发放日期(之前) */
	@ApiModelProperty(value="期望发放日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date expect_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date expect_date_old;
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description;
	private String description_old;
	
	/** 状态 */
	@ApiModelProperty(value="状态 0-草稿 1-申领",required=false)
	private Integer status;
	private Integer status_old;
	
	@ApiModelProperty(value="产品列表",required=false)
	private List<ApplyVoucherDetailForm> details;

	@ApiModelProperty(hidden = true)
	private Map<String,Object> info = new HashMap<String,Object>();
	
	@ApiModelProperty(value="不需要前端输入",hidden=true)
	public boolean isUpdate() {
		if(StringUtils.isNotEmpty(voucher_sn) || StringUtils.isNotEmpty(voucher_sn_old)) {
			info.put("voucher_sn", voucher_sn);
		}
//		if(StringUtils.isNotNull(proposer_inner_sn) || StringUtils.isNotNull(proposer_inner_sn_old)){
//			info.put("proposer_inner_sn", proposer_inner_sn);
//		}
		if(StringUtils.isNotNull(department_inner_sn) || StringUtils.isNotNull(department_inner_sn_old)) {
			info.put("department_inner_sn", department_inner_sn);
		}
		if(StringUtils.isNotNull(expect_date) || StringUtils.isNotNull(expect_date_old)) {
			if(StringUtils.isNull(expect_date)) {
				info.put("expect_date_null", 1);
			}
			info.put("expect_date", expect_date);
		}
		if(StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(description_old)) {
			info.put("description", description);
		}
		if(StringUtils.isNotEmpty(department_name) || StringUtils.isNotEmpty(department_name_old)) {
			info.put("department_name", department_name);
		}
		if(StringUtils.isNotNull(status) || StringUtils.isNotNull(status_old)) {
			info.put("status", status);
		}
		return StringUtils.isNotEmpty(info);
	}
	
	@ApiModelProperty(hidden = true)
	public Map<String,Object> getInfo(){
		return info;
	}
}
