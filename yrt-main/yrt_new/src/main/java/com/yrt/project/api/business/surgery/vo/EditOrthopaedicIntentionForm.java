package com.yrt.project.api.business.surgery.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Deprecated
public class EditOrthopaedicIntentionForm implements Serializable {

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
	@ApiModelProperty(value="开始日期",required=false)
	private Date begin_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date_old;
	/** 截止日期 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty(value="截止日期",required=false)
	private Date end_date;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date_old;
	/** 描述 */
	@ApiModelProperty(value="描述",required=false)
	private String description;
	private String description_old;
	
	/** 颅骨状态 */
	@ApiModelProperty(value="颅骨状态",required=false)
	private Integer t0_status;
	private Integer t0_status_old;
	/** 脊柱状态 */
	@ApiModelProperty(value="脊柱状态",required=false)
	private Integer t1_status;
	private Integer t1_status_old;
	/** 关节状态 */
	@ApiModelProperty(value="关节状态",required=false)
	private Integer t2_status;
	private Integer t2_status_old;
	/** 创伤状态 */
	@ApiModelProperty(value="创伤状态",required=false)
	private Integer t3_status;
	private Integer t3_status_old;
	
	@ApiModelProperty(hidden = true)
	private Map<String,Object> info = new HashMap<>();
	
	@ApiModelProperty(hidden = true)
	public Map<String,Object> getInfo(){
		info.put("hospital_inner_sn", hospital_inner_sn);
		info.put("dealer_inner_sn", dealer_inner_sn);
		
		return info;
	}
	
	public boolean hasUpdateInfo() {
		if(StringUtils.isNotNull(begin_date) || StringUtils.isNotNull(begin_date_old)) {
			if(StringUtils.isNull(begin_date)) {
				throw new ValidateException(MessageUtils.message("param.not.null", "开始日期"));
			}
			info.put("begin_date", begin_date);
		}
		if(StringUtils.isNotNull(end_date) || StringUtils.isNotNull(end_date_old)) {
			if(StringUtils.isNull(end_date)) {
				throw new ValidateException(MessageUtils.message("param.not.null", "截止日期"));
			}
			info.put("end_date", end_date);
		}
		if(StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(description_old)) {
			info.put("description", description);
		}
		
		return StringUtils.isNotEmpty(info);
	}
	
	public boolean hasUpdateT0Status() {
		if(StringUtils.isNotNull(t0_status) || StringUtils.isNotNull(t0_status_old)) {
			return true;
		}
		return false;
	}
	
	public boolean hasUpdateT1Status() {
		if(StringUtils.isNotNull(t1_status) || StringUtils.isNotNull(t1_status_old)) {
			return true;
		}
		return false;
	}
	
	public boolean hasUpdateT2Status() {
		if(StringUtils.isNotNull(t2_status) || StringUtils.isNotNull(t2_status_old)) {
			return true;
		}
		return false;
	}
	
	public boolean hasUpdateT3Status() {
		if(StringUtils.isNotNull(t3_status) || StringUtils.isNotNull(t3_status_old)) {
			return true;
		}
		return false;
	}
}
