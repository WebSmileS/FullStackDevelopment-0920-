package com.yrt.project.api.business.surgery.cust.vo;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import com.yrt.project.modular.surgery.cust.OrthopaedicCustOis;
import com.yrt.project.modular.surgery.cust.OrthopaedicCustOisProductDetail;

import io.swagger.annotations.ApiModelProperty;

/**
 * save ois(package) data fromat: ois & details + kitIds(excluding kit & its details)
 */
public class CustPackageRecord extends OrthopaedicCustOis {
	/**
	 * ois rels kit ids(sort_number decide by its order)
	 */
	@ApiModelProperty(value="手术包含的工具包id(s)")
	private List<Long> kitIds = new ArrayList<>();
	
	@Valid
	@ApiModelProperty(value="手术包产品明细")
	private List<OrthopaedicCustOisProductDetail> details = new ArrayList<>();

	public List<OrthopaedicCustOisProductDetail> getDetails() {
		return details;
	}

	public void setDetails(List<OrthopaedicCustOisProductDetail> details) {
		this.details = details;
	}

	public List<Long> getKitIds() {
		return kitIds;
	}

	public void setKitIds(List<Long> kitIds) {
		this.kitIds = kitIds;
	}

	@Override
	public String toString() {
		return super.toString() + "\n\t details: " + details + "\n\t kitIds: " + kitIds;
	}
}
