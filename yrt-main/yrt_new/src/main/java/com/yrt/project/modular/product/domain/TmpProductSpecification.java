package com.yrt.project.modular.product.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.yrt.common.utils.Utils;
import com.yrt.project.api.business.dealer.vo.tmpproduct.EditTmpProductSpecForm;
import com.yrt.project.api.platform.vo.product.MatchBean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TmpProductSpecification implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "数据来源 ")
	private Integer source;
	@ApiModelProperty(value = "送审机构内部编号")
	private Long operation_organization;
	@ApiModelProperty(value = "厂商内部编号 ")
	private Long vendor_inner_sn;
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	@ApiModelProperty(value = "型号规格内部编号")
	private Long specification_inner_sn;
	@ApiModelProperty(value = "型号规格")
	private String specification;
	@ApiModelProperty(value = "产品厂商自定编号(编码)")
	private String specification_vendor_sn;
	
	@ApiModelProperty(value = "操作时间")
	private Date operation_time;
	@ApiModelProperty(value = "审核描述")
	private String audit_description;
	@ApiModelProperty(value = "操作 0-删除 1-新增 2-修改")
	private Integer operation;
	@ApiModelProperty(value = "审核级别")
	private Integer audit_level;
	@ApiModelProperty(value = "是否送审产品")
	private Integer is_tmp;
	@ApiModelProperty(value = "状态 0-禁用 1-正常")
	private Integer status;
	@ApiModelProperty(value = "单位名称")
	private String unit_name;
	@ApiModelProperty(value = "医保编号")
	private String default_health_care_sn;
	@ApiModelProperty(value = "阳光流水号")
	private String default_sun_sn;

	public List<MatchBean> getMatchProperty(ProductSpecification oldSpec){
		List<MatchBean> result = new ArrayList<>();
		MatchBean bean1 = new MatchBean("型号规格", specification, oldSpec==null?"":oldSpec.getSpecification());
		result.add(bean1);
		MatchBean bean2 = new MatchBean("产品编码", specification_vendor_sn, oldSpec==null?"":oldSpec.getSpecification_vendor_sn());
		result.add(bean2);
		return result;
	}
	
	public boolean isUpdate(EditTmpProductSpecForm form) {
		if(Utils.equals(specification, form.getSpecification())
				&& Utils.equals(specification_vendor_sn, form.getSpecification_vendor_sn())) {
				return false;
		}
		return true;
	}
}
