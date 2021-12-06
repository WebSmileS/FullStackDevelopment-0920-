package com.yrt.project.api.platform.vo.organization;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddOrgRelImageForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 统一资源定位符 */
	@ApiModelProperty(value="统一资源定位符",required=true)
	@NotBlank(message = "统一资源定位符不能为空")
	private String url;
	/** 开始日期 */
	@ApiModelProperty(value="开始日期",required=false)
	private Date begin_date;
	/** 截止日期 */
	@ApiModelProperty(value="截止日期",required=false)
	private Date end_date;
	@ApiModelProperty(value="原文件名",required=true)
	private String file_name;
}
