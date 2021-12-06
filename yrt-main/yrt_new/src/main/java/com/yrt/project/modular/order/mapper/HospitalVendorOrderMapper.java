package com.yrt.project.modular.order.mapper;

import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface HospitalVendorOrderMapper extends OrderMapper {

	public void hv_add_hospital_po_vendor_so(Map<String,Object> params);
	
	public void hv_add_hospital_po_vendor_so_product_detail(Map<String,Object> params);
	
}
