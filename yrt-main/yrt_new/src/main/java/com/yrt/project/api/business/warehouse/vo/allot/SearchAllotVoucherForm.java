package com.yrt.project.api.business.warehouse.vo.allot;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchAllotVoucherForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="机构ID",required=true)
	@NotNull(message = "机构不能为空")
	private Long org_id;

	@ApiModelProperty(value="调拨单内部编号")
	private String av_inner_sn;

	@ApiModelProperty(value="调拨明细单内部编号")
	private String detail_inner_sn;

	@ApiModelProperty(value="申领单内部编号",notes = "当通过申请单检索调拨单时，需要该字段")
	private Long apply_voucher_inner_sn;

	@ApiModelProperty(value="单据编号")
	private String av_sn;

	@ApiModelProperty(value="产品名字")
	private String product_name;

	@ApiModelProperty(value = "调拨人内部编号")
	private Long poster_inner_sn;
	@ApiModelProperty(value="调拨入名字")
	private String poster_name;

	@ApiModelProperty(value = "确认人内部编号")
	private Long affirmant_inner_sn;
	@ApiModelProperty(value="确认入名字")
	private String affirmant_name;

	@ApiModelProperty(value = "日期区间(start)")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date start_date;

	@ApiModelProperty(value = "日期区间(end)")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;

	@ApiModelProperty(value="选项卡类型，1-待调出 2-待调入 3-调出历史 4-调入历史 默认为0",required=true)
	private Integer tab_id=0;
}