package com.yrt.project.modular.inquiry.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Inquiry implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "询价机构")
	private Long partA;
	@ApiModelProperty(value = "询价机构名称")
	private String partA_name;
	@ApiModelProperty(value = "询价单内部编号")
	private Long ilps_inner_sn;
	@ApiModelProperty(value = "被询价机构")
	private Long partB;
	@ApiModelProperty(value = "被询价机构名称")
	private String partB_name;
	/** 询价人内部编号
             该字段是预留的，可空，可以忽略 */
	@ApiModelProperty(value = "询价人内部编号")
	private Long inquirer_inner_sn;
	@ApiModelProperty(value = "询价人名称")
	private String inquirer_name;
	@ApiModelProperty(value = "状态")
	private String status;
	@ApiModelProperty(value = "询价时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date ask_time;
	@ApiModelProperty(value = "答复时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date answer_time;
	@ApiModelProperty(value = "询价单编号")
	private String ilps_sn;
	@ApiModelProperty(value = "联系人")
	private String contacts;
	@ApiModelProperty(value = "电话 ")
	private String phone;
	@ApiModelProperty(value = "描述")
	private String description;
	@ApiModelProperty(value="单据类型",required=true)
	private BillsType billsType;
	@ApiModelProperty(value = "询价单明细列表")
	private List<InquiryDetail> detailList;
}
