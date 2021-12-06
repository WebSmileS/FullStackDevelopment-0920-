package com.yrt.project.modular.finance.mapper;

import java.util.List;
import java.util.Map;

import com.yrt.project.modular.finance.domain.ReceiveOrder;
import com.yrt.project.modular.finance.domain.ReceiveOrderDetail;
import com.yrt.project.modular.finance.domain.SumOrderResult;

public interface ReceiveOrderMapper {

	public List<ReceiveOrder> selectReceiveOrderList(Map<String,Object> params);
	public ReceiveOrder selectReceiveOrderInfo(Map<String,Object> params);
	
	public void insertReceiveOrder(ReceiveOrder params);
	public void updateReceiveOrder(Map<String,Object> params);
	public void deleteReceiveOrder(Map<String,Object> params);
	
	public List<ReceiveOrderDetail> selectReceiveOrderDetailList(Map<String,Object> params);
	public List<SumOrderResult> sumUsableReceiveOrderDetail(Map<String,Object> params);
	public List<SumOrderResult> sumUsedReceiveOrderDetail(Map<String,Object> params);
	
	public void insertReceiveOrderDetail(ReceiveOrderDetail params);
	public void deleteReceiveOrderDetail(Map<String,Object> params);
}
