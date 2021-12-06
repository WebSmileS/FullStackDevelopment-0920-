package com.yrt.project.api.business.order.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.project.api.business.order.vo.order.AddPurchaseOrderForm;
import com.yrt.project.api.business.order.vo.order.AddSaleOrderForm;
import com.yrt.project.api.business.order.vo.order.ConfirmIDForm;
import com.yrt.project.api.business.order.vo.order.DetailCIDForm;
import com.yrt.project.api.business.order.vo.order.EditPurchaseOrderForm;
import com.yrt.project.api.business.order.vo.order.EditSaleOrderForm;
import com.yrt.project.api.business.order.vo.order.OrderIDForm;
import com.yrt.project.api.business.order.vo.order.OrderSendForm;
import com.yrt.project.api.business.order.vo.order.PurchaseHistorySearchForm;
import com.yrt.project.api.business.order.vo.order.PurchaseSearchForm;
import com.yrt.project.api.business.order.vo.order.SaleDetailSearchForm;
import com.yrt.project.api.business.order.vo.order.SaleHistorySearchForm;
import com.yrt.project.api.business.order.vo.order.SaleSearchForm;
import com.yrt.project.api.business.order.vo.order.SearchOrderPriceForm;
import com.yrt.project.api.business.order.vo.order.SearchPurchaseRecordForm;
import com.yrt.project.modular.order.domain.PurchaseOrder;
import com.yrt.project.modular.order.domain.PurchasingRecord;
import com.yrt.project.modular.order.domain.SaleOrder;
import com.yrt.project.modular.organization.domain.OrgEmployee;

public interface IOrderService {

	public PurchaseOrder addPurchaseOrder(AddPurchaseOrderForm form, ContextUserInfo curUser);
	
	public SaleOrder addSaleOrder(AddSaleOrderForm form, ContextUserInfo curUser);

	public PurchaseOrder editPurchaseOrder(EditPurchaseOrderForm form, ContextUserInfo info);
	
	public SaleOrder editSaleOrder(EditSaleOrderForm form, ContextUserInfo info);

	public int deleteOrder(List<OrderIDForm> list);

	public int stopOrder(List<OrderIDForm> list, ContextUserInfo curUser);
	
	public int confirmOrder(List<OrderIDForm> list, ContextUserInfo curUser);
	
	public int confirmSaleOrder(ConfirmIDForm form, ContextUserInfo curUser);

	public int confirmPurchaseOrder(ConfirmIDForm form, ContextUserInfo curUser);
	
	public int createPlan(OrderSendForm form, OrgEmployee emp);

	public SaleOrder selectSaleOrderInfo(OrderIDForm form);

	public List<SaleOrder> selectSaleOrderList(SaleSearchForm form, Long curEmployeeId);
	
	public List<SaleOrder> selectSaleOrderList4Phone(Long oid, String key, Integer status, Long curEmployeeId);
	
	public List<SaleOrder> selectHistorySaleOrderList(SaleHistorySearchForm form);
	
	public List<Map<String,Object>> selectSaleOrderDetailList(SaleDetailSearchForm form);

	public PurchaseOrder selectPurchaseOrderInfo(OrderIDForm form, OrgEmployee emp, boolean includeDetail);

	public List<PurchaseOrder> selectPurchaseOrderList(PurchaseSearchForm form, Long curEmployeeId);
	
	public List<PurchaseOrder> selectPurchaseOrderList4Phone(Long oid, String key, Integer status, Long curEmployeeId);
	
	public List<PurchaseOrder> selectHistoryPurchaseOrderList(PurchaseHistorySearchForm form);

	public int deleteDetailOrder(DetailCIDForm form);
	
	public int stopOrderDetail(DetailCIDForm form, ContextUserInfo curUser);

	public int unStopOrderDetail(DetailCIDForm form, ContextUserInfo curUser);

	public void updateTransferDetail(Long curOrgId, Long organization_inner_sn, Long voucher_inner_sn, Long voucher_details_inner_sn, 
			Long unitId, BigDecimal lockedQuantity, BillsType billsType, boolean isDelete, String update_by);

	public List<PurchasingRecord> selectPurchaseRecordList(SearchPurchaseRecordForm form, SystemType type);

	public List<BigDecimal> getOrderPrice(@Valid SearchOrderPriceForm form, Long orgId);

}
