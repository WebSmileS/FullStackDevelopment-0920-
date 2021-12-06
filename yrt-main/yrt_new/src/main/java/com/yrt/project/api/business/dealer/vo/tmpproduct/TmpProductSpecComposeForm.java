package com.yrt.project.api.business.dealer.vo.tmpproduct;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import com.yrt.project.api.business.dealer.vo.tmpproduct.TmpProductComposeForm.Add;
import com.yrt.project.api.business.dealer.vo.tmpproduct.TmpProductComposeForm.Update;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TmpProductSpecComposeForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="产品规格型号ID")
	@Null(groups= {Add.class}, message = "产品规格型号ID必须为空")
	@NotNull(groups= {Update.class},message = "产品规格型号ID不能为空")
	private Long specification_inner_sn;
	
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
	
	@ApiModelProperty(value="最小包装单位",required=true)
	@NotBlank(message = "最小包装单位不能为空")
	private String minUnit;
	
//	@ApiModelProperty(value = "医保编号")
//	private String default_health_care_sn;
//	@ApiModelProperty(value = "阳光流水号")
//	private String default_sun_sn;
}
