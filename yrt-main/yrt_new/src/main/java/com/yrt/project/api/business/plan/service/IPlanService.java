package com.yrt.project.api.business.plan.service;

import java.util.List;
import java.util.Map;

import com.yrt.common.utils.ContextUserInfo;
import com.yrt.project.api.business.applyvoucher.vo.AppApplayProductSearchForm;
import com.yrt.project.api.business.plan.vo.AddPlanForm;
import com.yrt.project.api.business.plan.vo.AppPlanSearchForm;
import com.yrt.project.api.business.plan.vo.BuildOrderForm;
import com.yrt.project.api.business.plan.vo.PlanDetailIDForm;
import com.yrt.project.api.business.plan.vo.EditPlanForm;
import com.yrt.project.api.business.plan.vo.IDForm;
import com.yrt.project.api.business.plan.vo.PlanSearchForm;
import com.yrt.project.api.business.plan.vo.PlanSearchProductForm;
import com.yrt.project.api.business.plan.vo.SearchBatchNumForm;
import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.order.domain.PurchaseOrder;
import com.yrt.project.modular.plan.domain.Plan;

public interface IPlanService {

	public Plan insertDealerPlan(AddPlanForm form);
	
	public Plan insertHospitalPlan(AddPlanForm form);

	public int updateDealerPlan(EditPlanForm form);
	
	public int updateHospitalPlan(EditPlanForm form);

	public int deletePlan(List<IDForm> list);

	public Plan selectPlanInfo(IDForm form);

	public List<Plan> searchPlanList(Long oid, PlanSearchForm form);
	
	public List<Plan> searchPlanList4Phone(AppPlanSearchForm form);

	public Map<String,Object> createOrderData(List<IDForm> list, Long oid);

	public int stopPlan(List<IDForm> list);

	public int deletePlanDetail(PlanDetailIDForm form);

	public int stopPlanDetail(PlanDetailIDForm form);
	
	public int unStopPlanDetail(PlanDetailIDForm form);

	public Map<String,Object> planDetailCreateOrderData(List<PlanDetailIDForm> list, Long oid);
	
	/**
	 * 获取厂商产品列表(有甲方合同关联的厂商产品)
	 * @return
	 */
	public List<ResponseProductInfo> selectVendorProductList(Long oid, PlanSearchProductForm form);
	public List<Map<String,Object>> selectVendorProductList4Phone(AppApplayProductSearchForm form);
	
	/**
	 * 获取经销商自建产品列表(有甲方合同关联的经销商自建产品)
	 * @return
	 */
	public List<ResponseProductInfo> selectDealerProductList(Long oid, PlanSearchProductForm form);
	public List<Map<String,Object>> selectDealerProductList4Phone(AppApplayProductSearchForm form);

	public List<PurchaseOrder> buildOrder(BuildOrderForm form, ContextUserInfo curUser);

	//所有合同双方产品列表
	public List<Map<String, Object>> selectContractVendorProductList4Phone(AppApplayProductSearchForm form);
	public List<Map<String, Object>> selectContractDealerProductList4Phone(AppApplayProductSearchForm form);

	public List<String> selectProductBatchNumList(SearchBatchNumForm form);

}
