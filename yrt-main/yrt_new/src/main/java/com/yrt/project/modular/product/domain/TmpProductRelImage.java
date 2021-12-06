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
public class TmpProductRelImage implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "数据来源 ")
	private Integer source;
	@ApiModelProperty(value = "送审机构内部编号")
	private Long operation_organization;
	@ApiModelProperty(value = "厂商内部编号 ")
	private Long vendor_inner_sn;
	@ApiModelProperty(value = "产品内部编号")
	private Long product_inner_sn;
	/** 图片内部编号 */
	@ApiModelProperty(value = "图片内部编号")
	private Long image_inner_sn;
	/** 类别 */
	@ApiModelProperty(value = "图片类别")
	private Integer type;
	/** 开始日期 */
	@ApiModelProperty(value = "开始日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;
	/** 截止日期 */
	@ApiModelProperty(value = "截止日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;
	/** 统一资源定位符 */
	@ApiModelProperty(value = "统一资源定位符")
	private String url;

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

	public List<MatchBean> getMatchProperty(ProductRelImage oldImage) {
		List<MatchBean> result = new ArrayList<>();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//		MatchBean bean1 = new MatchBean("type", type.toString(), oldImage == null ? "" : oldImage.getType().toString());
		MatchBean bean2 = new MatchBean("开始日期", begin_date == null ? "" : f.format(begin_date),
				(oldImage == null||oldImage.getBegin_date() == null) ? "" : f.format(oldImage.getBegin_date()));
		MatchBean bean3 = new MatchBean("截止日期", end_date == null ? "" : f.format(end_date),
				(oldImage == null||oldImage.getEnd_date() == null) ? "" : f.format(oldImage.getEnd_date()));
		MatchBean bean4 = new MatchBean("图片", url.toString(), oldImage == null ? "" : oldImage.getUrl().toString());
//		result.add(bean1);
		result.add(bean2);
		result.add(bean3);
		result.add(bean4);
		return result;
	}
}
