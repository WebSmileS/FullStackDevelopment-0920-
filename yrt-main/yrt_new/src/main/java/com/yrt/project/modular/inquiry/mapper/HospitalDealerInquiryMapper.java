package com.yrt.project.modular.inquiry.mapper;

import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface HospitalDealerInquiryMapper extends InquiryMapper {

	public void h_add_hospital_dealer_il(Map<String,Object> params);
	
	public void h_add_hospital_dealer_il_product_detail(Map<String,Object> params);
}
