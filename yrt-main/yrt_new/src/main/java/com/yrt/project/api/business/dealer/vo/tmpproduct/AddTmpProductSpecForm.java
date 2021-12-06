package com.yrt.project.api.business.dealer.vo.tmpproduct;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddTmpProductSpecForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="数据来源 0来自平台;1来自医院;2来自经销商;3来自厂商",required=true)
	@NotNull(message = "数据来源不能为空")
	private Integer source;
	
	@ApiModelProperty(value="厂商ID",required=true)
	@NotNull(message = "厂商ID不能为空")
	private Long vendor_inner_sn;
	
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message = "产品不能为空")
	private Long product_inner_sn;
	
	@ApiModelProperty(value="规格型号",required=true)
	@NotBlank(message = "规格型号不能为空")
	private String specifications;
	
	@ApiModelProperty(value="编码",required=false)
	private String specification_vendor_sn;
	
	@ApiModelProperty(value="操作 0-删除 1-新增 2-修改",required=true)
	@NotNull(message = "操作不能为空")
	private Integer operation;
	
	@ApiModelProperty(value="审核级别 0-一级 1-二级 2-三级 3-四级",required=true)
	@NotNull(message = "审核级别不能为空")
	private Integer audit_level;
	
	@ApiModelProperty(value="状态 2-草稿 3-待审核",required=true)
	@NotNull(message = "状态不能为空")
	private Integer status;
	
	@ApiModelProperty(value = "单位名称")
	private String unit_name = "";
	
	@ApiModelProperty(value = "医保编号")
	private String default_health_care_sn;
	
	@ApiModelProperty(value = "阳光流水号")
	private String default_sun_sn;
}
