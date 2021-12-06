package com.yrt.project.modular.external.mapper;

import java.util.Map;

public interface ExternalOrderMapper {

	public void insertPurchaseOrder(Map<String, Object> params);
	
	public void insertPurchaseOrderVendorDetail(Map<String, Object> params);
	public void insertPurchaseOrderDealerDetail(Map<String, Object> params);
}
