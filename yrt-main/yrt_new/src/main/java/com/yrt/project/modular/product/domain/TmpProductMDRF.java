package com.yrt.project.modular.product.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.project.api.platform.vo.product.MatchBean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TmpProductMDRF implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "数据来源 ")
	private Integer source;
	@ApiModelProperty(value = "送审机构内部编号")
	private Long operation_organization;
	@ApiModelProperty(value = "厂商内部编号 ")
	private Long vendor_inner_sn;
	@ApiModelProperty(value = "厂商名称")
	private String vendor_name;
	@ApiModelProperty(value = "厂商地址")
	private String vendor_address;
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	@ApiModelProperty(value = "产品名称")
	private String product_name;
	@ApiModelProperty(value = "登记表内部编号")
	private Long mdrf_inner_sn;
	@ApiModelProperty(value = "颁发日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date issue_date;
	@ApiModelProperty(value = "开始日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	@ApiModelProperty(value = "截止日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
	@ApiModelProperty(value = "注册号")
	private String register_sn;
	@ApiModelProperty(value = "发证部门")
	private String issue_organization;
	@ApiModelProperty(value = "产品标准")
	private String standard;
	@ApiModelProperty(value = "生产地址")
	private String production_address;
	@ApiModelProperty(value = "性能结构及组成")
	private String performance_structure;
	@ApiModelProperty(value = "适用范围")
	private String application_range;
	@ApiModelProperty(value = "禁忌症")
	private String contraindication;
	@ApiModelProperty(value = "描述")
	private String description;
	@ApiModelProperty(value = "型号规格(总览)")
	private String specifications;

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
	@ApiModelProperty(value = "是否存在注册证图片")
	private Boolean hasCertImage;

	private List<TmpProductMDRFImage> images = new ArrayList<>();

	public List<MatchBean> getMatchProperty(ProductMDRF oldMDRF) {
		List<MatchBean> result = new ArrayList<>();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		MatchBean bean1 = new MatchBean("发证日期", issue_date == null ? "" : f.format(issue_date),
				(oldMDRF==null||oldMDRF.getIssue_date() == null) ? "" : f.format(oldMDRF.getIssue_date()));
		MatchBean bean2 = new MatchBean("开始日期", begin_date == null ? "" : f.format(begin_date),
				(oldMDRF==null||oldMDRF.getBegin_date() == null) ? "" : f.format(oldMDRF.getBegin_date()));
		MatchBean bean3 = new MatchBean("截止日期", end_date == null ? "" : f.format(end_date),
				(oldMDRF==null||oldMDRF.getEnd_date() == null) ? "" : f.format(oldMDRF.getEnd_date()));
		MatchBean bean4 = new MatchBean("注册号", register_sn, oldMDRF==null?"":oldMDRF.getRegister_sn());
		MatchBean bean5 = new MatchBean("发证部门", issue_organization, oldMDRF==null?"":oldMDRF.getIssue_organization());
		MatchBean bean6 = new MatchBean("产品标准", standard, oldMDRF==null?"":oldMDRF.getStandard());
		MatchBean bean7 = new MatchBean("生产地址", production_address, oldMDRF==null?"":oldMDRF.getProduction_address());
		MatchBean bean8 = new MatchBean("性能结构及组成", performance_structure,
				oldMDRF==null?"":oldMDRF.getPerformance_structure());
		MatchBean bean9 = new MatchBean("适用范围", application_range, oldMDRF==null?"":oldMDRF.getApplication_range());
		MatchBean bean10 = new MatchBean("禁忌症", contraindication, oldMDRF==null?"":oldMDRF.getContraindication());
		MatchBean bean11 = new MatchBean("描述", description, oldMDRF==null?"":oldMDRF.getDescription());
		MatchBean bean12 = new MatchBean("型号规格", specifications, oldMDRF==null?"":oldMDRF.getSpecifications());
		result.add(bean1);
		result.add(bean2);
		result.add(bean3);
		result.add(bean4);
		result.add(bean5);
		result.add(bean6);
		result.add(bean7);
		result.add(bean8);
		result.add(bean9);
		result.add(bean10);
		result.add(bean11);
		result.add(bean12);
		return result;
	}
}
