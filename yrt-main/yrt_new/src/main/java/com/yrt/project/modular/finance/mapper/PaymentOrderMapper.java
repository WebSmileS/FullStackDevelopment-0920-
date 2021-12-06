package com.yrt.project.modular.finance.mapper;

import java.util.List;
import java.util.Map;

import com.yrt.project.modular.finance.domain.PaymentOrder;
import com.yrt.project.modular.finance.domain.PaymentOrderDetail;
import com.yrt.project.modular.finance.domain.SumOrderResult;

public interface PaymentOrderMapper {

	public List<PaymentOrder> selectPaymentOrderList(Map<String,Object> params);
	public PaymentOrder selectPaymentOrderInfo(Map<String,Object> params);
	
	public void insertPaymentOrder(PaymentOrder params);
	public void updatePaymentOrder(Map<String,Object> params);
	public void deletePaymentOrder(Map<String,Object> params);
	
	public List<PaymentOrderDetail> selectPaymentOrderDetailList(Map<String,Object> params);
	public List<SumOrderResult> sumUsablePaymentOrderDetail(Map<String,Object> params);
	public List<SumOrderResult> sumUsedPaymentOrderDetail(Map<String,Object> params);
	
	public void insertPaymentOrderDetail(PaymentOrderDetail params);
	public void deletePaymentOrderDetail(Map<String,Object> params);
	
}
