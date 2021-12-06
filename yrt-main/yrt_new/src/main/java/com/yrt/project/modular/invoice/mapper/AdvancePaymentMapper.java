package com.yrt.project.modular.invoice.mapper;

import java.util.List;
import java.util.Map;

import com.yrt.project.modular.invoice.domain.AdvancePayment;
import com.yrt.project.modular.invoice.domain.PaymentAttachment;

public interface AdvancePaymentMapper {

	public List<AdvancePayment> selectAdvancePaymentList(Map<String,Object> params);
	
	public AdvancePayment selectAdvancePaymentInfo(Map<String,Object> params);
	
	public void insertAdvancePayment(AdvancePayment params);
	
	public void updateAdvancePayment(Map<String,Object> params);
	
	public void deleteAdvancePayment(Map<String,Object> params);
	
	
	public List<PaymentAttachment> selectPaymentAttachmentList(Map<String,Object> params);
	
	public void insertPaymentAttachment(PaymentAttachment params);
	public void deletePaymentAttachment(Map<String,Object> params);
}
