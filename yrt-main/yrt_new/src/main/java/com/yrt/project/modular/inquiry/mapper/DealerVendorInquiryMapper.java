package com.yrt.project.modular.inquiry.mapper;

import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface DealerVendorInquiryMapper extends InquiryMapper {

	public void d_add_dealer_vendor_il(Map<String,Object> params);
	
	public void d_add_dealer_vendor_il_product_detail(Map<String,Object> params);
}
