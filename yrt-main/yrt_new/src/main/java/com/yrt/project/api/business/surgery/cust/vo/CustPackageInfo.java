package com.yrt.project.api.business.surgery.cust.vo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.yrt.project.modular.surgery.cust.OrthopaedicCustOis;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustOisProductDetail;

import io.swagger.annotations.ApiModelProperty;

/**
 * get ois(package) data format(ois & details, and kits(including kit details))
 */
public class CustPackageInfo extends OrthopaedicCustOis {

	public static CustPackageInfo of(OrthopaedicCustOis record, List<OrthopaedicCustOisProductDetail> details,
			List<CustKitInfo> kits) {
		CustPackageInfo info = new CustPackageInfo();
		BeanUtils.copyProperties(record, info);
		info.setDetails(details);
		info.setKits(kits);
		return info;
	}

	@ApiModelProperty(value="手术包含的工具包", required=false)
	private List<CustKitInfo> kits = new ArrayList<>();
	
	@ApiModelProperty(value="手术包产品明细", required=false)
	private List<OrthopaedicCustOisProductDetail> details = new ArrayList<>();

	public List<CustKitInfo> getKits() {
		return kits;
	}

	public void setKits(List<CustKitInfo> kits) {
		this.kits = kits;
	}

	public List<OrthopaedicCustOisProductDetail> getDetails() {
		return details;
	}

	public void setDetails(List<OrthopaedicCustOisProductDetail> details) {
		this.details = details;
	}

}
