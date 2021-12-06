package com.yrt.project.api.business.inquiry.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddInquiryForm implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 询价机构 */
	@ApiModelProperty(value="询价机构ID",required=true)
	@NotNull(message = "询价机构ID不能为空")
	private Long partA;
	/** 被询价机构 */
	@ApiModelProperty(value="被询价机构ID",required=true)
	@NotNull(message = "被询价机构ID不能为空")
	private Long partB;
	/** 询价人内部编号
             该字段是预留的，可空，可以忽略 */
	@ApiModelProperty(value="询价人ID",required=false)
	private Long inquirer_inner_sn;
	/** 状态 0-询价草稿 1-询价  2-回复草稿 3-已回复 */
	@ApiModelProperty(value="状态 0-询价草稿 1-询价  2-回复草稿 3-已回复",required=true)
	@NotNull(message = "状态不能为空")
	private Integer status;
	/** 询价单编号 */
	@ApiModelProperty(value="询价单编号",required=false)
	private String ilps_sn;
	/** 联系人 */
	@ApiModelProperty(value="联系人",required=false)
	private String contacts = "";
	/** 电话 */
	@ApiModelProperty(value="电话",required=false)
	private String phone = "";
	/** 描述 */
	@ApiModelProperty(value="摘要",required=false)
	private String description = "";
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
	
	@ApiModelProperty(value="询价产品列表",required=false)
	private List<AddInquiryDetailForm> detailList = new ArrayList<>();
}
