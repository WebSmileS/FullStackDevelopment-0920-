package com.yrt.project.api.common.vo;

import java.util.Date;

import com.alibaba.druid.util.StringUtils;

import lombok.Data;

@Data
public class UDIDInfo {

	private Long product_vendor_inner_sn;
	private String product_vendor_name;
	private Long product_inner_sn;
	private String product_name;
	private Long specification_inner_sn;
	private String specification;
	private String udi_barcode;
	private String whole_barcode;
	private Date produce_date;
	private Date overdue_date;
	private String batch_number;
	private String serial_number;
	
	public boolean includeUDI() {
		return StringUtils.isEmpty(udi_barcode)?false:true;
	}
}
