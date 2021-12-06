package com.yrt.project.api.business.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.ReceiveStatus;
import com.yrt.common.dict.RefVoucherType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.CompressedIdGenerator;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.Utils;
import com.yrt.project.api.business.finance.vo.PaymentDetailForm;
import com.yrt.project.api.business.finance.vo.ReceiveForm;
import com.yrt.project.api.business.finance.vo.SearchReceiveForm;
import com.yrt.project.api.business.invoice.InvoiceService;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.modular.finance.domain.ReceiveOrder;
import com.yrt.project.modular.finance.domain.ReceiveOrderDetail;
import com.yrt.project.modular.finance.domain.SumOrderResult;
import com.yrt.project.modular.finance.mapper.ReceiveOrderMapper;

@Service
public class ReceiveOrderService {
	
	@Autowired ReceiveOrderMapper mapper;
	@Autowired VoucherNumberService numberUtils;
	
	@Autowired InvoiceService invoiceService;

	private void checkReceiveStatus(Long id, boolean isCancel) {
		checkReceiveStatus(id, isCancel, false);
	}
	
	private void checkReceiveStatus(Long id, boolean isCancel, boolean isRecorded) {
		if(Utils.isEmpty(id)) {
			throw new ValidateException("收款单ID不能为空!!");
		}
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		ReceiveOrder info = mapper.selectReceiveOrderInfo(params);
		if(Utils.isEmpty(info)) {
			throw new ValidateException("错误的收款单ID!!");
		}
		if(isCancel) {
			if(!ReceiveStatus.RECEIVED.equals(info.getReceive_status())) {
				throw new ValidateException("只有已收款的收款单才可以作废!!");
			}
		}else {
			if(isRecorded) {
				if(!ReceiveStatus.RECEIVED.equals(info.getReceive_status())) {
					throw new ValidateException("只有已收款状态的收款单才可以入账!!");
				}
			}else { 
				if(!ReceiveStatus.TO_BE_COLLECTED.equals(info.getReceive_status())) {
					throw new ValidateException("只有待收款状态的收款单才可以编辑和删除!!");
				}
			}
		}
	}
	
	@Transactional
	public ReceiveOrder insertReceive(ReceiveForm form, ContextUserInfo curUser) {
		String sn = numberUtils.getMaxSn(VoucherType.RECEIVE_ORDER, curUser.getSystemType(), curUser.getOrgId());
		ReceiveOrder entity = Utils.clone(form, ReceiveOrder.class);
		entity.setId(CompressedIdGenerator.generateId());
		entity.setOrg_id(curUser.getOrgId());
		entity.setSystem_type(curUser.getSystemType().value());
		entity.setReceive_sn(sn);
		entity.setCreated_by(curUser.getEmployee().getName());
		entity.setCreated_time(new Date());
		if(ReceiveStatus.RECEIVED.equals(form.getReceive_status())) {
			entity.setReceive_by(curUser.getEmployee().getName());
			entity.setReceive_date(new Date());
		}
		mapper.insertReceiveOrder(entity);
		List<PaymentDetailForm> details = form.getDetails();
		saveReceiveOrderDetail(entity.getId(), details);
		
		if(ReceiveStatus.RECEIVED.equals(form.getReceive_status())) {
			pay(entity.getId(), curUser);
		}
		return selectReceiveInfo(entity.getId());
	}
	
	@Transactional
	private void saveReceiveOrderDetail(Long receiveOrderId, List<PaymentDetailForm> details) {
		for (PaymentDetailForm detailForm : details) {
			if(BigDecimal.ZERO.compareTo(detailForm.getWritten_off_price()) >= 0 ) {
				throw new ValidateException("核销金额必须大于0!");
			}
			if(detailForm.getInvoice_price().compareTo(detailForm.getWritten_off_price()) < 0) {
				throw new ValidateException("核销金额不能大于发票金额!");
			}
			ReceiveOrderDetail detail = Utils.clone(detailForm, ReceiveOrderDetail.class);
			detail.setId(CompressedIdGenerator.generateId());
			detail.setReceive_order_id(receiveOrderId);
			mapper.insertReceiveOrderDetail(detail);
		}
	}

	@Transactional
	public ReceiveOrder updateReceive(ReceiveForm form, ContextUserInfo curUser) {
		checkReceiveStatus(form.getId(), false);
		Map<String,Object> params = new HashMap<>();
		params.put("id", form.getId());
		params.put("bills_type", form.getBills_type());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());
		params.put("organization_name", form.getOrganization_name());
		params.put("receive_date", form.getReceive_date());
		params.put("receive_price", form.getReceive_price());
		params.put("description", form.getDescription());
		params.put("receive_method", form.getReceive_method());
		params.put("receive_status", form.getReceive_status());
		if(ReceiveStatus.RECEIVED.equals(form.getReceive_status())) {
			params.put("receive_by", curUser.getEmployee().getName());
			params.put("receive_time", new Date());
		}
		mapper.updateReceiveOrder(params);
		
		params.put("receive_order_id", form.getId());
		mapper.deleteReceiveOrderDetail(params);
		List<PaymentDetailForm> details = form.getDetails();
		saveReceiveOrderDetail(form.getId(), details);
		
		if(ReceiveStatus.RECEIVED.equals(form.getReceive_status())) {
			pay(form.getId(), curUser);
		}
		return selectReceiveInfo(form.getId());
	}

	@Transactional
	public int pay(List<Long> list, ContextUserInfo curUser) {
		for (Long id : list) {
			checkReceiveStatus(id, false);//检查是否错误的ID
			pay(id, curUser);
		}
		return 1;
	}
	
	@Transactional
	private void pay(Long id, ContextUserInfo curUser) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("receive_status", ReceiveStatus.RECEIVED);
		params.put("receive_by", curUser.getEmployee().getName());
		params.put("receive_time", new Date());
		mapper.updateReceiveOrder(params);
		updateRefVoucherStatus(id, curUser.getOrgId());
	}

	@Transactional
	public int recorded(List<Long> list, ContextUserInfo curUser) {
		Map<String,Object> params = new HashMap<>();
		for (Long id : list) {
			checkReceiveStatus(id, false, true);//检查是否错误的ID
			params.put("id", id);
			params.put("receive_status", ReceiveStatus.RECORDED);
			params.put("entry_by", curUser.getEmployee().getName());
			params.put("entry_time", new Date());
			mapper.updateReceiveOrder(params);
		}
		return 1;
	}
	
	@Transactional
	private void updateRefVoucherStatus(Long id, Long orgId) {
		List<ReceiveOrderDetail> details = searchReceiveDetailList(id);
		for (ReceiveOrderDetail detail : details) {
			Map<String,Object> params = new HashMap<>();
			params.put("org_id", orgId);
			params.put("voucher_type_c", detail.getVoucher_type_c());
			params.put("voucher_inner_sn_c", detail.getVoucher_inner_sn_c());
			List<SumOrderResult> results = mapper.sumUsedReceiveOrderDetail(params);
			for (SumOrderResult res : results) {
				invoiceService.updateInvoiceStatus(res.getVoucher_inner_sn_c(), res.getWritten_off_price());
			}
		}
	}
	
	@Transactional
	private void rollbackVoucherStatus(Long id, Long orgId) {
		List<ReceiveOrderDetail> details = searchReceiveDetailList(id);
		for (ReceiveOrderDetail detail : details) {
			invoiceService.rollbackInvoiceStatus(detail.getVoucher_inner_sn_c(), detail.getWritten_off_price());
		}
	}

	@Transactional
	public int cancelReceive(Long id, String reason, ContextUserInfo curUser) {
		checkReceiveStatus(id, true);//检查是否错误的ID
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		params.put("is_cancel", 1);
		params.put("cancel_reason", reason);
		params.put("cancel_by", curUser.getEmployee().getName());
		params.put("cancel_time", new Date());
		mapper.updateReceiveOrder(params);
		rollbackVoucherStatus(id, curUser.getOrgId());
		return 1;
	}

	@Transactional
	public int deleteReceive(List<Long> list) {
		Map<String,Object> params = new HashMap<>();
		for (Long id : list) {
			checkReceiveStatus(id, false);//检查是否错误的ID
			params.put("id", id);
			params.put("receive_order_id", id);
			mapper.deleteReceiveOrderDetail(params);
			mapper.deleteReceiveOrder(params);
		}
		return 1;
	}

	public ReceiveOrder selectReceiveInfo(Long id) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		ReceiveOrder info = mapper.selectReceiveOrderInfo(params);
		if(Utils.isEmpty(info)) {
			throw new ValidateException("错误的收款ID");
		}
		params.put("receive_order_id", id);
		List<ReceiveOrderDetail> details = mapper.selectReceiveOrderDetailList(params);
		info.setDetails(details);
		return info;
	}

	public List<ReceiveOrder> searchReceiveList(SearchReceiveForm form, ContextUserInfo curUser) {
		return searchReceiveList(form, curUser, false);
	}

	public List<ReceiveOrder> searchReceiveList(SearchReceiveForm form, ContextUserInfo curUser, boolean isExport) {
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", curUser.getOrgId());
		params.put("system_type", curUser.getSystemType());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());
		params.put("receive_sn", form.getReceive_sn());
		params.put("bills_type", form.getBillsType());
		params.put("is_cancel", form.getIsCancel());
		if(isExport) {
			params.put("is_cancel", null);
		}
		if(ReceiveStatus.ALL.equals(form.getReceive_status())) {
			params.put("receive_status", "1,2,3");
		}else {
			params.put("receive_status", form.getReceive_status().value());
		}
		params.put("begin_date", form.getBegin_date());
		params.put("end_date", form.getEnd_date());
		return mapper.selectReceiveOrderList(params);
	}
	
	public List<ReceiveOrderDetail> searchReceiveDetailList(Long id) {
		Map<String,Object> params = new HashMap<>();
		params.put("receive_order_id", id);
		return mapper.selectReceiveOrderDetailList(params);
	}

	public BigDecimal getUsedInvoicePrice(long orgId, Long systemInvoiceId, RefVoucherType voucherType) {
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", orgId);
		params.put("voucher_type_c", voucherType.value());
		params.put("voucher_inner_sn_c", systemInvoiceId);
		List<SumOrderResult> sum = mapper.sumUsableReceiveOrderDetail(params);
		if(Utils.isNotEmpty(sum)) {
			return sum.get(0).getWritten_off_price();
		}
		return BigDecimal.ZERO;
	}
}
