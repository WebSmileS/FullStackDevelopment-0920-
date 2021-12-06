package com.yrt.project.api.business.surgery.cust.vo;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;

import com.yrt.project.modular.surgery.cust.OrthopaedicCustKit;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustKitProductDetail;

import io.swagger.annotations.ApiModelProperty;

/**
 * save & get: kit data format(all in one).
 */
public class CustKitInfo extends OrthopaedicCustKit {
	
	public static CustKitInfo of(OrthopaedicCustKit record, List<OrthopaedicCustKitProductDetail> details) {
		CustKitInfo info = new CustKitInfo();
		BeanUtils.copyProperties(record, info);
		info.setDetails(details);
		return info;
	}

	@Valid
	@ApiModelProperty(value="工具包产品明细")
	private List<OrthopaedicCustKitProductDetail> details = new ArrayList<>();

	public List<OrthopaedicCustKitProductDetail> getDetails() {
		return details;
	}

	public void setDetails(List<OrthopaedicCustKitProductDetail> details) {
		this.details = details;
	}
	
	
}
