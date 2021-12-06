package com.yrt.project.api.business.inquiry.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;

import com.yrt.common.dict.BillsType;
import com.yrt.common.utils.StringUtils;
import com.yrt.project.api.common.service.VoucherNumberService;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditInquiryForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(hidden = true)
	@Autowired private VoucherNumberService numberUtils;

	/** 询价机构 */
	@ApiModelProperty(value="询价机构ID",required=true)
	@NotNull(message = "询价机构ID不能为空")
	private Long partA;
	/** 询价单内部编号 */
	@ApiModelProperty(value="询价单内部编号",required=true)
	@NotNull(message = "询价单内部编号不能为空")
	private Long ilps_inner_sn;
	/** 被询价机构 */
	@ApiModelProperty(value="被询价机构ID",required=false)
	private Long partB;
	private Long partB_old;
	/** 状态 0-询价草稿 1-询价  2-回复草稿 3-已回复 */
	@ApiModelProperty(value="状态",required=true)
	@NotNull(message = "状态不能为空")
	private Integer status;
	/** 询价单编号 */
//	@ApiModelProperty(value="询价单编号",required=false)
//	private String ilps_sn;
//	private String ilps_sn_old;
	/** 联系人 */
	@ApiModelProperty(value="联系人",required=false)
	private String contacts;
	private String contacts_old;
	/** 电话 */
	@ApiModelProperty(value="电话",required=false)
	private String phone;
	private String phone_old;
	/** 描述 */
	@ApiModelProperty(value="摘要",required=false)
	private String description;
	private String description_old;
	@ApiModelProperty(value="新单据类型",required=true)
	@NotNull(message = "新单据类型不能为空")
	private BillsType billsType;
	
	@ApiModelProperty(value="旧单据类型",required=true)
	@NotNull(message = "旧单据类型不能为空")
	private BillsType billsType_old;
	
	@ApiModelProperty(value="询价产品列表",required=false)
	private List<AddInquiryDetailForm> detailList = new ArrayList<>();
	
	@ApiModelProperty(hidden = true)
	private Map<String,Object> info = new HashMap<String,Object>();
	
	@ApiModelProperty(hidden = true)
	public Map<String,Object> getInfo(){
//		if(StringUtils.isNotEmpty(ilps_sn) || StringUtils.isNotEmpty(ilps_sn_old)){
//			if(StringUtils.isEmpty(ilps_sn)) {
//				ilps_sn = numberUtils.getMaxSn(VoucherType.INQUIRY_PRICE, ServletUtils.getCurUserSystemType(), partA);//.getInquiryMaxSn(ServletUtils.getCurUserSystemType(), partA);
//			}
//			info.put("ilps_sn", ilps_sn);
//		}
		if(StringUtils.isNotEmpty(contacts) || StringUtils.isNotEmpty(contacts_old)){
			info.put("contacts", contacts);
		}
		if(StringUtils.isNotEmpty(phone) || StringUtils.isNotEmpty(phone_old)){
			info.put("phone", phone);
		}
		if(StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(description_old)){
			info.put("description", description);
		}
		if(StringUtils.isNotNull(partB) || StringUtils.isNotNull(partB_old)){
			info.put("partB", partB);
		}
		if(status == 3) {
			info.put("answer_time", new Date());
		}
		return info;
	}
}
