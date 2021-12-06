package com.yrt.project.modular.inquiry.mapper;

import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface HospitalVendorInquiryMapper extends InquiryMapper {

	public void h_add_hospital_vendor_il(Map<String,Object> params);
	
	public void h_add_hospital_vendor_il_product_detail(Map<String,Object> params);
}
