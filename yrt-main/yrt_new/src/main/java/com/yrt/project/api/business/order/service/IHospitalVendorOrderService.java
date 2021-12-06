package com.yrt.project.api.business.order.service;

import com.yrt.common.utils.ContextUserInfo;
import com.yrt.project.api.business.order.vo.order.AddPurchaseOrderForm;
import com.yrt.project.api.business.order.vo.order.AddSaleOrderForm;
import com.yrt.project.api.business.order.vo.order.DetailCIDForm;
import com.yrt.project.api.business.order.vo.order.EditPurchaseOrderForm;
import com.yrt.project.api.business.order.vo.order.EditSaleOrderForm;
import com.yrt.project.api.business.order.vo.order.OrderIDForm;
import com.yrt.project.modular.order.domain.PurchaseOrder;
import com.yrt.project.modular.order.domain.SaleOrder;

public interface IHospitalVendorOrderService {

	public PurchaseOrder addPurchaseOrder(AddPurchaseOrderForm form, int system_type1, int system_type2);
	
	public SaleOrder addSaleOrder(AddSaleOrderForm form, int system_type1, int system_type2);

	public PurchaseOrder editPurchaseOrder(EditPurchaseOrderForm form, ContextUserInfo info);
	
	public SaleOrder editSaleOrder(EditSaleOrderForm form);

	public int detailBuildSaleOrder(DetailCIDForm form);

	public SaleOrder selectSaleOrderInfo(OrderIDForm form);
	
	public PurchaseOrder selectPurchaseOrderInfo(OrderIDForm form);
}
