package com.yrt.system.quartz.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class JobSearchForm implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="定时任务名称",required=false)
	private String jobName;
	
	@ApiModelProperty(value="定时任务状态 0-正常 1-暂停",required=false)
	private String status;
	
	@ApiModelProperty(value="定时任务方法名称",required=false)
	private String methodName;
}
