package com.yrt.project.api.business.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.PaymentStatus;
import com.yrt.common.dict.RefVoucherType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.CompressedIdGenerator;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.Utils;
import com.yrt.project.api.business.finance.vo.PaymentDetailForm;
import com.yrt.project.api.business.finance.vo.PaymentForm;
import com.yrt.project.api.business.finance.vo.SearchPaymentForm;
import com.yrt.project.api.business.invoice.AdvancePaymentService;
import com.yrt.project.api.business.invoice.InvoiceService;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.modular.finance.domain.PaymentOrder;
import com.yrt.project.modular.finance.domain.PaymentOrderDetail;
import com.yrt.project.modular.finance.domain.SumOrderResult;
import com.yrt.project.modular.finance.mapper.PaymentOrderMapper;

@Service
public class PaymentOrderService {
	
	@Autowired PaymentOrderMapper mapper;
	@Autowired VoucherNumberService numberUtils;
	
	@Autowired AdvancePaymentService advanceService;
	@Autowired InvoiceService invoiceService;

	private void checkPaymentStatus(Long id, boolean isCancel) {
		checkPaymentStatus(id, isCancel, false);
	}
	
	private void checkPaymentStatus(Long id, boolean isCancel, boolean isRecorded) {
		if(Utils.isEmpty(id)) {
			throw new ValidateException("付款单ID不能为空!!");
		}
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		PaymentOrder info = mapper.selectPaymentOrderInfo(params);
		if(Utils.isEmpty(info)) {
			throw new ValidateException("错误的付款单ID!!");
		}
		if(isCancel) {
			if(!PaymentStatus.PAID.equals(info.getPayment_status())) {
				throw new ValidateException("只有已付款的付款单才可以作废!!");
			}
		}else {
			if(isRecorded) {
				if(!PaymentStatus.PAID.equals(info.getPayment_status())) {
					throw new ValidateException("只有已付款状态的付款单才可以入账!!");
				}
			}else { 
				if(!PaymentStatus.TO_BE_PAID.equals(info.getPayment_status())) {
					throw new ValidateException("只有待付款状态的付款单才可以编辑和删除!!");
				}
			}
		}
	}
	
	@Transactional
	public PaymentOrder insertPayment(PaymentForm form, ContextUserInfo curUser) {
		String sn = numberUtils.getMaxSn(VoucherType.PAYMENT_ORDER, curUser.getSystemType(), curUser.getOrgId());
		PaymentOrder entity = Utils.clone(form, PaymentOrder.class);
		entity.setId(CompressedIdGenerator.generateId());
		entity.setOrg_id(curUser.getOrgId());
		entity.setSystem_type(curUser.getSystemType().value());
		entity.setPayment_sn(sn);
		entity.setCreated_by(curUser.getEmployee().getName());
		entity.setCreated_time(new Date());
		if(PaymentStatus.PAID.equals(form.getPayment_status())) {
			entity.setPayment_by(curUser.getEmployee().getName());
			entity.setPayment_time(new Date());
		}
		mapper.insertPaymentOrder(entity);
		List<PaymentDetailForm> details = form.getDetails();
		savePaymentOrderDetail(entity.getId(), details);
		
		if(PaymentStatus.PAID.equals(form.getPayment_status())) {
			pay(entity.getId(), curUser);
		}
		return selectPaymentInfo(entity.getId());
	}
	
	@Transactional
	private void savePaymentOrderDetail(Long paymentOrderId, List<PaymentDetailForm> details) {
		for (PaymentDetailForm detailForm : details) {
			if(BigDecimal.ZERO.compareTo(detailForm.getWritten_off_price()) >= 0 ) {
				throw new ValidateException("核销金额必须大于0!");
			}
			if(detailForm.getInvoice_price().compareTo(detailForm.getWritten_off_price()) < 0) {
				throw new ValidateException("核销金额不能大于发票金额!");
			}
			PaymentOrderDetail detail = Utils.clone(detailForm, PaymentOrderDetail.class);
			detail.setId(CompressedIdGenerator.generateId());
			detail.setPayment_order_id(paymentOrderId);
			mapper.insertPaymentOrderDetail(detail);
		}
	}

	@Transactional
	public PaymentOrder updatePayment(PaymentForm form, ContextUserInfo curUser) {
		checkPaymentStatus(form.getId(), false);
		Map<String,Object> params = new HashMap<>();
		params.put("id", form.getId());
		params.put("bills_type", form.getBills_type());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());
		params.put("organization_name", form.getOrganization_name());
		params.put("payment_date", form.getPayment_date());
		params.put("payment_price", form.getPayment_price());
		params.put("description", form.getDescription());
		params.put("payment_method", form.getPayment_method());
		params.put("payment_status", form.getPayment_status());
		if(PaymentStatus.PAID.equals(form.getPayment_status())) {
			params.put("payment_by", curUser.getEmployee().getName());
			params.put("payment_time", new Date());
		}
		params.put("payment_order_id", form.getId());
		mapper.deletePaymentOrderDetail(params);
		
		List<PaymentDetailForm> details = form.getDetails();
		savePaymentOrderDetail(form.getId(), details);
		
		mapper.updatePaymentOrder(params);
		if(PaymentStatus.PAID.equals(form.getPayment_status())) {
			pay(form.getId(), curUser);
		}
		
		return selectPaymentInfo(form.getId());
	}

	@Transactional
	public int pay(List<Long> list, ContextUserInfo curUser) {
		for (Long id : list) {
			checkPaymentStatus(id, false);//检查是否错误的ID
			pay(id, curUser);
		}
		return 1;
	}
	
	@Transactional
	private int pay(Long id, ContextUserInfo curUser) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("payment_status", PaymentStatus.PAID);
		params.put("payment_by", curUser.getEmployee().getName());
		params.put("payment_time", new Date());
		mapper.updatePaymentOrder(params);
		
		updateRefVoucherStatus(id, curUser.getOrgId());
		return 1;
	}

	@Transactional
	public int recorded(List<Long> list, ContextUserInfo curUser) {
		Map<String,Object> params = new HashMap<>();
		for (Long id : list) {
			checkPaymentStatus(id, false, true);//检查是否错误的ID
			params.put("id", id);
			params.put("payment_status", PaymentStatus.RECORDED);
			params.put("entry_by", curUser.getEmployee().getName());
			params.put("entry_time", new Date());
			mapper.updatePaymentOrder(params);
		}
		return 1;
	}
	
	@Transactional
	private void updateRefVoucherStatus(Long id, Long orgId) {
		List<PaymentOrderDetail> details = searchPaymentDetailList(id);
		for (PaymentOrderDetail detail : details) {
			Map<String,Object> params = new HashMap<>();
			params.put("org_id", orgId);
			params.put("voucher_type_c", detail.getVoucher_type_c());
			params.put("voucher_inner_sn_c", detail.getVoucher_inner_sn_c());
			List<SumOrderResult> results = mapper.sumUsedPaymentOrderDetail(params);
			for (SumOrderResult res : results) {
				if(RefVoucherType.GRN_INVOICE.value() == res.getVoucher_type_c().intValue()) {
					invoiceService.updateInvoiceStatus(res.getVoucher_inner_sn_c(), res.getWritten_off_price());
				}
				if(RefVoucherType.ADVANCE_PAYMENT.value() == res.getVoucher_type_c().intValue()) {
					advanceService.updateInvoiceStatus(res.getVoucher_inner_sn_c(), res.getWritten_off_price());
				}
			}
		}
	}
	
	@Transactional
	private void rollbackVoucherStatus(Long id, Long orgId) {
		List<PaymentOrderDetail> details = searchPaymentDetailList(id);
		for (PaymentOrderDetail detail : details) {
			if(RefVoucherType.GRN_INVOICE.value() == detail.getVoucher_type_c().intValue()) {
				invoiceService.rollbackInvoiceStatus(detail.getVoucher_inner_sn_c(), detail.getWritten_off_price());
			}
			if(RefVoucherType.ADVANCE_PAYMENT.value() == detail.getVoucher_type_c().intValue()) {
				advanceService.rollbackInvoiceStatus(detail.getVoucher_inner_sn_c(), detail.getWritten_off_price());
			}
		}
	}
	
	@Transactional
	public int cancelPayment(Long id, String reason, ContextUserInfo curUser) {
		checkPaymentStatus(id, true);//检查是否错误的ID
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("is_cancel", 1);
		params.put("cancel_reason", reason);
		params.put("cancel_by", curUser.getEmployee().getName());
		params.put("cancel_time", new Date());
		mapper.updatePaymentOrder(params);
		rollbackVoucherStatus(id, curUser.getOrgId());
		return 1;
	}

	@Transactional
	public int deletePayment(List<Long> list) {
		Map<String,Object> params = new HashMap<>();
		for (Long id : list) {
			checkPaymentStatus(id, false);//检查是否错误的ID
			params.put("id", id);
			params.put("payment_order_id", id);
			mapper.deletePaymentOrderDetail(params);
			mapper.deletePaymentOrder(params);
		}
		return 1;
	}

	public PaymentOrder selectPaymentInfo(Long id) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		PaymentOrder info = mapper.selectPaymentOrderInfo(params);
		if(Utils.isEmpty(info)) {
			throw new ValidateException("错误的预付款ID");
		}
		params.put("payment_order_id", id);
		List<PaymentOrderDetail> details = mapper.selectPaymentOrderDetailList(params);
		info.setDetails(details);
		return info;
	}

	public List<PaymentOrder> searchPaymentList(SearchPaymentForm form, ContextUserInfo curUser) {
		return searchPaymentList(form, curUser, false);
	}

	public List<PaymentOrder> searchPaymentList(SearchPaymentForm form, ContextUserInfo curUser, boolean isExport) {
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", curUser.getOrgId());
		params.put("system_type", curUser.getSystemType());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());
		params.put("payment_sn", form.getPayment_sn());
		params.put("is_cancel", form.getIsCancel());
		params.put("bills_type", form.getBillsType());
		if(isExport) {
			params.put("is_cancel", null);
		}
		if(PaymentStatus.ALL.equals(form.getPayment_status())) {
			params.put("payment_status", "1,2,3");
		}else {
			params.put("payment_status", form.getPayment_status().value());
		}
		params.put("begin_date", form.getBegin_date());
		params.put("end_date", form.getEnd_date());
		return mapper.selectPaymentOrderList(params);
	}

	public List<PaymentOrderDetail> searchPaymentDetailList(Long id) {
		Map<String,Object> params = new HashMap<>();
		params.put("payment_order_id", id);
		return mapper.selectPaymentOrderDetailList(params);
	}
	
	public BigDecimal getUsedInvoicePrice(Long orgId, Long systemInvoiceId, RefVoucherType voucherType){
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", orgId);
		params.put("voucher_type_c", voucherType.value());
		params.put("voucher_inner_sn_c", systemInvoiceId);
		List<SumOrderResult> sum = mapper.sumUsablePaymentOrderDetail(params);
		if(Utils.isNotEmpty(sum)) {
			return sum.get(0).getWritten_off_price();
		}
		return BigDecimal.ZERO;
	}
}
