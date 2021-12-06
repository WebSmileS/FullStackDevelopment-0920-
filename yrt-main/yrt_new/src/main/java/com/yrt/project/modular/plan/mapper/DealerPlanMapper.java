package com.yrt.project.modular.plan.mapper;

import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface DealerPlanMapper extends PlanMapper {

	public void d_add_dealer_ppl(Map<String,Object> params);
	
	public void d_add_dealer_ppl_product_detail(Map<String,Object> params);
	
	public void d_add_dealer_ppl_dealer_product_detail(Map<String,Object> params);
}
