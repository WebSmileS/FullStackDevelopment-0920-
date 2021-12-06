package com.yrt.project.modular.plan.mapper;

import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface HospitalPlanMapper extends PlanMapper {

	public void h_add_hospital_ppl(Map<String,Object> params);
	
	public void h_add_hospital_ppl_product_detail(Map<String,Object> params);
	
	public void h_add_hospital_ppl_dealer_product_detail(Map<String,Object> params);
}
