package com.yrt.project.modular.order.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.contract.domain.ContractProduct;
import com.yrt.project.modular.order.domain.OrderDetail;
import com.yrt.project.modular.order.domain.PurchaseOrder;
import com.yrt.project.modular.order.domain.PurchaseOrderDetail;
import com.yrt.project.modular.order.domain.SaleOrder;

@Repository
public interface OrderMapper {

	public PurchaseOrder selectPurchaseOrderInfo(Map<String,Object> params);
	
	public List<PurchaseOrder> searchPurchaseOrderList(Map<String,Object> params);
	public List<PurchaseOrder> searchPurchaseOrderList4Phone(Map<String,Object> params);
	public List<PurchaseOrder> searchPurchaseOrderList4Phone1(Map<String,Object> params);
	
	//查询采购记录
	public List<PurchaseOrderDetail> selectPurchaseOrderDetail(Map<String,Object> params);
	
	public SaleOrder selectSaleOrderInfo(Map<String,Object> params);
	
	public List<Map<String,Object>> selectSaleOrderDetailList(Map<String,Object> params);
	
	public List<SaleOrder> searchSaleOrderList(Map<String,Object> params);
	public List<SaleOrder> searchSaleOrderList4Phone(Map<String,Object> params);
	public List<SaleOrder> searchSaleOrderList4Phone1(Map<String,Object> params);
	
	public int updateOrder(Map<String,Object> params);
	
	public int deleteOrder(Map<String,Object> params);
	
	public List<OrderDetail> selectOrderDetailList(Map<String,Object> params);
	
	public int updateDealerOrderDetail(Map<String,Object> params);
	
	public int updateVendorOrderDetail(Map<String,Object> params);
	
	public int deleteDealerOrderDetail(Map<String,Object> params);
	
	public int deleteVendorOrderDetail(Map<String,Object> params);
	
	//检查订单明细是否已全部完成
	public List<Map<String,Object>> checkExistOrderDetail(Map<String, Object> params);
	
	//获取订单产品价格列表
	public List<ContractProduct> selectOrderProductDetail(Map<String, Object> params);
	public List<ContractProduct> selectOrderDealerProductDetail(Map<String, Object> params);
}
