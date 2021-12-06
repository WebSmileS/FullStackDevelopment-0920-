package com.yrt.project.modular.order.mapper;

import java.util.List;
import java.util.Map;

import com.yrt.project.modular.order.domain.OrderDetail;
import com.yrt.project.modular.order.domain.PurchaseOrder;

public interface PurchasingRecordInitMapper {

	public List<PurchaseOrder> searchPurchaseOrderList();
	
	public List<OrderDetail> selectDDOrderDetailList(Map<String,Object> params);
	public List<OrderDetail> selectDVOrderDetailList(Map<String,Object> params);
	public List<OrderDetail> selectHDOrderDetailList(Map<String,Object> params);
	public List<OrderDetail> selectHVOrderDetailList(Map<String,Object> params);
}
