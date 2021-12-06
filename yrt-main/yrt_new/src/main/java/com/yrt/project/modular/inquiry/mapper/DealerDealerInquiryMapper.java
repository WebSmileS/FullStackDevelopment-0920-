package com.yrt.project.modular.inquiry.mapper;

import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface DealerDealerInquiryMapper extends InquiryMapper {

	public void d_add_dealer_dealer_il(Map<String,Object> params);
	
	public void d_add_dealer_dealer_il_product_detail(Map<String,Object> params);
}
