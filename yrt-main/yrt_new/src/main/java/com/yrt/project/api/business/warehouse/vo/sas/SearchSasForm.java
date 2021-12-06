package com.yrt.project.api.business.warehouse.vo.sas;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchSasForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message="当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="单据编号",required=false)
	private String sas_sn;
	
	@ApiModelProperty(value="仓库ID",required=false)
	private Long warehouse_inner_sn;
	
	@ApiModelProperty(value="作业人名称",required=false)
	private String operator_name;
	
	@ApiModelProperty(value="起始日期",required=false)
	private Date begin_date;
	
	@ApiModelProperty(value="结束日期",required=false)
	private Date end_date;
	
	@ApiModelProperty(value="是否只显示有效数据,默认值true",required=false)
	private Boolean isOnlyEffective = true;
	
	@ApiModelProperty(value="是否自己的任务，默认false",required=false)
	private Boolean onlySelf = false;

	@ApiModelProperty(value="状态",required=false)
	private Integer status ;
}
