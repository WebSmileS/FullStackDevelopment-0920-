package com.yrt.project.modular.order.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.order.domain.SaleOrder;

@Repository
public interface DealerVendorOrderMapper extends OrderMapper {

	public void dv_add_dealer_po_vendor_so(Map<String,Object> params);
	
	public void dv_add_dealer_po_vendor_so_product_detail(Map<String,Object> params);
	
	public List<SaleOrder> searchSaleHistoryOrderList(Map<String,Object> params);
	
}
