package com.yrt.project.modular.surgery.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrthopaedicIntentionKey {
	
	public static OrthopaedicIntentionKey of(Long hid, Long did) {
		OrthopaedicIntentionKey key = new OrthopaedicIntentionKey();
		key.setHospital_inner_sn(hid);
		key.setDealer_inner_sn(did);
		return key;
	}
	
	@ApiModelProperty(value = "医院内部编号")
	private Long hospital_inner_sn;
	
	@ApiModelProperty(value = "经销商内部编号")
	private Long dealer_inner_sn;
	
}
