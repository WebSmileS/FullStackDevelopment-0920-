package com.yrt.project.modular.product.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.yrt.project.api.platform.vo.product.MatchBean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TmpProduct implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "厂商内部编号 ")
	private Long vendor_inner_sn;
	@ApiModelProperty(value = "厂商名称")
	private String vendor_name;
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	@ApiModelProperty(value = "自定义分类内部编号")
	private Long type_inner_sn;
	@ApiModelProperty(value = "自定义分类名称")
	private String type_name;
	@ApiModelProperty(value = "医疗器械分类目录编号")
	private Long code68_sn;
	@ApiModelProperty(value = "医疗器械分类目录名称")
	private String code68_name;
	@ApiModelProperty(value = "数据来源 ")
	private Integer source;
	@ApiModelProperty(value = "状态")
	private Integer status;
	@ApiModelProperty(value = "存储条件")
	private Integer storage_condition;
	@ApiModelProperty(value = "医保类型")
	private Integer health_care_type;
	@ApiModelProperty(value = "产品类型 0-医疗器械 1-非医疗器械")
	private Integer type;
	@ApiModelProperty(value = "产品名称")
	private String name;
	@ApiModelProperty(value = "医疗器械分类目录类别:1-一类 2-二类 3-三类 ")
	private Integer code68_type;
	@ApiModelProperty(value = "厂商产品编号")
	private String product_vendor_sn;
	@ApiModelProperty(value = "型号规格(总览)")
	private String specifications;
	@ApiModelProperty(value = "描述")
	private String description;
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
	@ApiModelProperty(value = "送审机构内部编号")
	private Long operation_organization;

	@ApiModelProperty(value = "送审产品规格型号列表")
	private List<TmpProductSpecification> tmpSpecList = new ArrayList<>();
	@ApiModelProperty(value = "送审产品图片列表")
	private List<TmpProductRelImage> tmpImageList = new ArrayList<>();
	@ApiModelProperty(value = "送审产品注册登记证列表")
	private List<TmpProductMDRF> tmpMDRFeList = new ArrayList<>();

	public List<MatchBean> getMatchProperty(Product oldProduct) {
		List<MatchBean> result = new ArrayList<>();
		MatchBean bean1 = new MatchBean("类型", type_name == null ? "" : type_name,
				(oldProduct==null||oldProduct.getType_name() == null) ? "" : oldProduct.getType_name());
		MatchBean bean2 = new MatchBean("产品分类", code68_sn == null ? "" : code68_sn.toString() + code68_name,
				(oldProduct==null||oldProduct.getCode68_sn() == null) ? "" : oldProduct.getCode68_sn().toString() + oldProduct.getCode68_name());
		MatchBean bean3 = new MatchBean("存储条件",
				storage_condition == null ? "" : storage_condition.toString(),
				(oldProduct==null||oldProduct.getStorage_condition() == null) ? "" : oldProduct.getStorage_condition().toString());
		MatchBean bean4 = new MatchBean("医保类别", health_care_type == null ? "" : health_care_type.toString(),
				(oldProduct==null||oldProduct.getHealth_care_type() == null) ? "" : oldProduct.getHealth_care_type().toString());
		MatchBean bean5 = new MatchBean("产品类型", type.toString(), oldProduct==null?"":oldProduct.getType().toString());
		MatchBean bean6 = new MatchBean("产品名称", name, oldProduct==null?"":oldProduct.getName());
		MatchBean bean10 = new MatchBean("医疗器械分类目录类别", code68_type == null ? "" : code68_type.toString(), 
				(oldProduct==null||oldProduct.getCode68_type()==null)?"":oldProduct.getCode68_type().toString());
		MatchBean bean8 = new MatchBean("备注", description, oldProduct==null?"":oldProduct.getDescription());
		MatchBean bean9 = new MatchBean("型号规格(总览)", specifications, oldProduct==null?"":oldProduct.getSpecifications());
		result.add(bean1);
		result.add(bean2);
		result.add(bean3);
		result.add(bean4);
		result.add(bean5);
		result.add(bean6);
		result.add(bean8);
		result.add(bean9);
		result.add(bean10);
		return result;
	}
}
