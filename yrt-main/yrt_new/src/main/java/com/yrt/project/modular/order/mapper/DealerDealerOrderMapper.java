package com.yrt.project.modular.order.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.order.domain.PurchaseOrder;
import com.yrt.project.modular.order.domain.SaleOrder;

@Repository
public interface DealerDealerOrderMapper extends OrderMapper {

	public void dd_add_dealer_po_dealer_so(Map<String,Object> params);
	
	public void dd_add_dealer_po_dealer_so_product_detail(Map<String,Object> params);
	
	public void dd_add_dealer_po_dealer_so_dealer_product_detail(Map<String,Object> params);
	
	public List<PurchaseOrder> searchPurchaseHistoryOrderList(Map<String,Object> params);
	
	public List<SaleOrder> searchSaleHistoryOrderList(Map<String,Object> params);
}
