package com.yrt.project.modular.order.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.order.domain.PurchaseOrder;

@Repository
public interface HospitalDealerOrderMapper extends OrderMapper {

	public void hd_add_hospital_po_dealer_so(Map<String,Object> params);
	
	public void hd_add_hospital_po_dealer_so_product_detail(Map<String,Object> params);
	
	public void hd_add_hospital_po_dealer_so_dealer_product_detail(Map<String,Object> params);

	public List<PurchaseOrder> searchPurchaseHistoryOrderList(Map<String,Object> params);
}
