package com.yrt.project.modular.plan.mapper;

import java.util.List;
import java.util.Map;

import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.plan.domain.BuildOrderData;
import com.yrt.project.modular.plan.domain.Plan;
import com.yrt.project.modular.plan.domain.PlanDetail;
import com.yrt.project.modular.plan.domain.SupplierData4Out;

public interface PlanMapper {

	public int updatePlan(Map<String,Object> params);
	
	public int deletePlan(Map<String,Object> params);
	
	public Plan selectPlanInfo(Map<String,Object> params);
	
	public List<Plan> searchPlanList(Map<String,Object> params);
	
	public int updatePlanVendorDetail(Map<String,Object> params);
	
	public int deletePlanVendorDetail(Map<String,Object> params);
	
	public int updatePlanDealerDetail(Map<String,Object> params);
	
	public int deletePlanDealerDetail(Map<String,Object> params);
	
	public List<PlanDetail> selectPlanDetailList(Map<String,Object> params);
	public List<BuildOrderData> selectPlanProductDetailList(Map<String,Object> params);
	public List<BuildOrderData> selectPlanDearlerProductDetailList(Map<String,Object> params);
	public List<Map<String,Object>> selectPlanDetailIDList(Map<String,Object> params);
	public List<SupplierData4Out> selectSupplierInfo(Map<String,Object> params);
	public List<SupplierData4Out> selectSupplierInfo1(Map<String,Object> params);
	
	//获取厂商产品列表(有合同关联的厂商产品)
	public List<ResponseProductInfo> selectVendorProductList(Map<String,Object> params);
	public List<Map<String, Object>> selectVendorProductList4Phone(Map<String,Object> params);
	//获取经销商自建产品列表(有合同关联的经销商)
	public List<ResponseProductInfo> selectDealerProductList(Map<String,Object> params);
	public List<Map<String, Object>> selectDealerProductList4Phone(Map<String,Object> params);
	
	//所有合同双方产品列表
	public List<Map<String, Object>> selectContractVendorProductList4Phone(Map<String,Object> params);
	public List<Map<String, Object>> selectContractDealerProductList4Phone(Map<String,Object> params);

	public List<String> selectProductBatchNumList(Map<String, Object> params);
	public List<String> selectDealerProductBatchNumList(Map<String, Object> params);
}
