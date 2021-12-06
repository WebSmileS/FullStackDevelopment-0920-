package com.yrt.project.api.business.invoice;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.yrt.common.dict.InvoiceStatus;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.CompressedIdGenerator;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.Utils;
import com.yrt.project.api.business.finance.vo.ApproveAdvancePaymentForm;
import com.yrt.project.api.business.finance.vo.SearchBusinessAdvancePaymentForm;
import com.yrt.project.api.business.invoice.vo.AddAdvancePaymentForm;
import com.yrt.project.api.business.invoice.vo.AttachmentForm;
import com.yrt.project.api.business.invoice.vo.EditAdvancePaymentForm;
import com.yrt.project.api.business.invoice.vo.SearchInvoiceForm;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.modular.invoice.domain.AdvancePayment;
import com.yrt.project.modular.invoice.domain.PaymentAttachment;
import com.yrt.project.modular.invoice.mapper.AdvancePaymentMapper;

@Service
public class AdvancePaymentService {

	@Autowired AdvancePaymentMapper mapper;
	@Autowired VoucherNumberService numberUtils;
	
	@Transactional
	public AdvancePayment insertAdvancePayment(@Validated AddAdvancePaymentForm form, ContextUserInfo curUser) {
		String sn = numberUtils.getMaxSn(VoucherType.ADVANCE_PAYMENT, curUser.getSystemType(), curUser.getOrgId());
		AdvancePayment entity = Utils.clone(form, AdvancePayment.class);
		entity.setId(CompressedIdGenerator.generateId());
		entity.setOrg_id(curUser.getOrgId());
		entity.setSystem_type(curUser.getSystemType().value());
		entity.setPayment_sn(sn);
		entity.setCreated_by(curUser.getEmployee().getName());
		entity.setCreated_time(new Date());
		mapper.insertAdvancePayment(entity);
		return entity;
	}
	
	@Transactional
	public AdvancePayment updateAdvancePayment(@Validated EditAdvancePaymentForm form, ContextUserInfo curUser) {
		checkAdvancePaymentStatus(form.getId(), form.getPayment_status());
		Map<String,Object> params = new HashMap<>();
		params.put("id", form.getId());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());
		params.put("organization_name", form.getOrganization_name());
		if(Utils.isNotEmpty(form.getHandler_inner_sn())) {
			params.put("handler_inner_sn", form.getHandler_inner_sn());
			params.put("handler_name", form.getHandler_name());
		}else {
			params.put("handler_inner_sn", -1);
			params.put("handler_name", "");
		}
		if(Utils.isNotEmpty(form.getDepartment_inner_sn())) {
			params.put("department_inner_sn", form.getDepartment_inner_sn());
			params.put("department_name", form.getDepartment_name());
		}else {
			params.put("department_inner_sn", -1);
			params.put("department_name", "");
		}
		params.put("bills_type", form.getBills_type());
		params.put("payment_date", form.getPayment_date());
		params.put("payment_price", form.getPayment_price());
		params.put("description", form.getDescription());
		params.put("payment_method", form.getPayment_method());
		params.put("payment_status", form.getPayment_status());
		mapper.updateAdvancePayment(params);
		
		return selectAdvancePaymentInfo(form.getId());
	}
	
	@Transactional
	public int submitAdvancePayment(List<Long> list, ContextUserInfo curUser) {
		Map<String,Object> params = new HashMap<>();
		for (Long id : list) {
			checkAdvancePaymentStatus(id, InvoiceStatus.PENDING);//检查是否错误的ID
			params.put("id", id);
			params.put("payment_status", InvoiceStatus.PENDING);
			mapper.updateAdvancePayment(params);
		}
		return 1;
	}
	
	@Transactional
	public int deleteAdvancePayment(List<Long> list) {
		Map<String,Object> params = new HashMap<>();
		for (Long id : list) {
			checkAdvancePaymentStatus(id, false, true);//检查是否错误的ID
			params.put("id", id);
			params.put("payment_id", id);
			mapper.deletePaymentAttachment(params);
			mapper.deleteAdvancePayment(params);
		}
		return 1;
	}
	
	public AdvancePayment selectAdvancePaymentInfo(Long id) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		AdvancePayment info = mapper.selectAdvancePaymentInfo(params);
		if(Utils.isEmpty(info)) {
			throw new ValidateException("错误的预付款ID");
		}
		params.put("payment_id", id);
		List<PaymentAttachment> attas = mapper.selectPaymentAttachmentList(params);
		info.setAttas(attas);
		return info;
	}
	
	public List<AdvancePayment> searchAdvancePaymentList(SearchInvoiceForm form, ContextUserInfo curUser) {
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", curUser.getOrgId());
		params.put("system_type", curUser.getSystemType());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());
		params.put("bills_type", form.getBillsType());
		params.put("payment_sn", form.getInvoice_no());
		if(InvoiceStatus.ALL.equals(form.getInvoice_status())) {
			params.put("payment_status", "1,2,3,4,5");
		}else{
			params.put("payment_status", form.getInvoice_status().value());
		}
		params.put("is_cancel", form.getIsCancel());
		params.put("begin_date", form.getBegin_date());
		params.put("end_date", form.getEnd_date());
		return mapper.selectAdvancePaymentList(params);
	}
	
	public List<AdvancePayment> searchAdvancePaymentList(SearchBusinessAdvancePaymentForm form, ContextUserInfo curUser, boolean isExport) {
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", curUser.getOrgId());
		params.put("system_type", curUser.getSystemType());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());
		params.put("bills_type", form.getBillsType());
		params.put("payment_sn", form.getInvoice_no());
		params.put("is_cancel", form.getIsCancel());
		if(isExport) {
			params.put("is_cancel", null);
			params.put("payment_status", "2,3,4,5");
		}else {
			if(form.getIsCancel() == 0) {
				if(InvoiceStatus.NOT_WRITTEN_OFF.equals(form.getInvoice_status())) {
					params.put("payment_status", "3,4");
				}else {
					params.put("payment_status", form.getInvoice_status().value());
				}
			}else {
				params.put("payment_status", "2,3,4,5");
			}
		}
		params.put("begin_date", form.getBegin_date());
		params.put("end_date", form.getEnd_date());
		return mapper.selectAdvancePaymentList(params);
	}
	private void checkAdvancePaymentStatus(Long id, boolean isCancel) {
		checkAdvancePaymentStatus(id, isCancel, false);
	}
	
	private void checkAdvancePaymentStatus(Long id, boolean isCancel, boolean isDelete) {
		if(Utils.isEmpty(id)) {
			throw new ValidateException("预付款ID不能为空!!");
		}
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		AdvancePayment info = mapper.selectAdvancePaymentInfo(params);
		if(Utils.isEmpty(info)) {
			throw new ValidateException("错误的预付款ID!!");
		}
		InvoiceStatus oldStatus = info.getPayment_status();
		if(isCancel) {
			if(!InvoiceStatus.PENDING.equals(oldStatus) 
					&& !InvoiceStatus.NOT_WRITTEN_OFF.equals(oldStatus)) {
				throw new ValidateException("只有待审核状态和未核销的预付款才可以作废!!");
			}
		}else {
			if(isDelete) {
				if(!InvoiceStatus.DRAFT.equals(oldStatus)
						&& !InvoiceStatus.PENDING.equals(oldStatus)) {
					throw new ValidateException("只有草稿和待审核状态的预付款才可以删除!!");
				}
			}else {
				if(!InvoiceStatus.DRAFT.equals(oldStatus)) {
					throw new ValidateException("只有草稿状态的预付款才可以编辑!!");
				}
			}
		}
	}
	
	private void checkAdvancePaymentStatus(Long id, InvoiceStatus newStatus) {
		if(Utils.isEmpty(id)) {
			throw new ValidateException("预付款ID不能为空!!");
		}
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		AdvancePayment info = mapper.selectAdvancePaymentInfo(params);
		if(Utils.isEmpty(info)) {
			throw new ValidateException("错误的预付款ID!!");
		}
		InvoiceStatus oldStatus = info.getPayment_status();
		switch (oldStatus) {
		case DRAFT:
			if(!InvoiceStatus.DRAFT.equals(newStatus)
					&& !InvoiceStatus.PENDING.equals(newStatus)) {
				throw new ValidateException("只有草稿状态的预付款才可以编辑和删除!!");
			}
			break;
		case PENDING:
			if(!InvoiceStatus.NOT_WRITTEN_OFF.equals(newStatus)) {
				throw new ValidateException("只有待审核状态的预付款才可以审批!!");
			}
			break;
		default:
			break;
		}
	}

	@Transactional
	public int approveAdvancePayment(ApproveAdvancePaymentForm form, ContextUserInfo curUser) {
		Map<String,Object> params = new HashMap<>();
		checkAdvancePaymentInfo(form);//检查是否错误的ID
		params.put("id", form.getId());
		params.put("payment_date", form.getPayment_date());
		params.put("payment_price", form.getPayment_price());
		params.put("description", form.getDescription());
		params.put("payment_method", form.getPayment_method());
		params.put("payment_status", InvoiceStatus.NOT_WRITTEN_OFF);
		params.put("confirm_by", curUser.getEmployee().getName());
		params.put("confirm_time", new Date());
		mapper.updateAdvancePayment(params);
		//清除所有附件
		Map<String,Object> params1 = new HashMap<>();
		params1.put("payment_id", form.getId());
		mapper.deletePaymentAttachment(params1);
		List<AttachmentForm> attas = form.getAttas();
		for (AttachmentForm attaForm : attas) {
			PaymentAttachment atta = Utils.clone(attaForm, PaymentAttachment.class);
			atta.setId(CompressedIdGenerator.generateId());
			atta.setPayment_id(form.getId());
			atta.setCreated_by(curUser.getEmployee().getName());
			atta.setCreated_time(new Date());
			mapper.insertPaymentAttachment(atta);
		}
		return 1;
	}

	private void checkAdvancePaymentInfo(ApproveAdvancePaymentForm form) {
		checkAdvancePaymentStatus(form.getId(), InvoiceStatus.NOT_WRITTEN_OFF);//检查是否错误的ID
		BigDecimal price = form.getPayment_price();
		if(Utils.isEmpty(form.getAttas())){
			throw new ValidateException("付款凭证的附件必须上传!!");
		}
		if(price == null) {
			throw new ValidateException("发票金额和税率不能为空!!");
		}else {
			if(BigDecimal.ZERO.compareTo(price) > 0) {
				throw new ValidateException("发票金额不能小于等于0!!");
			}
		}
	}

	@Transactional
	public int cancelAdvancePayment(Long id, String reason, ContextUserInfo curUser) {
		Map<String,Object> params = new HashMap<>();
		checkAdvancePaymentStatus(id, true);//检查是否错误的ID
		params.put("id", id);
		params.put("is_cancel", 1);
		params.put("cancel_reason", reason);
		params.put("cancel_by", curUser.getEmployee().getName());
		params.put("cancel_time", new Date());
		mapper.updateAdvancePayment(params);
		return 1;
	}

	@Transactional
	public void updateInvoiceStatus(Long voucher_inner_sn_c, BigDecimal written_off_price) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", voucher_inner_sn_c);
		AdvancePayment info = mapper.selectAdvancePaymentInfo(params);
		BigDecimal fix = info.getPayment_price().subtract(written_off_price);
		switch (fix.compareTo(BigDecimal.ZERO)) {
		case -1:
			throw new ValidateException(String.format("预付款%s超额核销%f,请检查付款单!",info.getPayment_sn(),fix.setScale(2, BigDecimal.ROUND_CEILING)));
		case 0:
			params.put("payment_status", InvoiceStatus.WRITTEN_OFF);
			break;
		case 1:
			params.put("payment_status", InvoiceStatus.PARTIAL_WRITE_OFF);
			break;
		}
		if(BigDecimal.ZERO.compareTo(written_off_price) == 0) {
			params.put("payment_status", InvoiceStatus.NOT_WRITTEN_OFF);
		}
		params.put("written_off_price", written_off_price);
		mapper.updateAdvancePayment(params);
	}
	
	@Transactional
	public void rollbackInvoiceStatus(Long voucher_inner_sn_c, BigDecimal written_off_price) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", voucher_inner_sn_c);
		AdvancePayment info = mapper.selectAdvancePaymentInfo(params);
		BigDecimal fix = info.getWritten_off_price().subtract(written_off_price);
		switch (fix.compareTo(BigDecimal.ZERO)) {
		case -1:
			throw new ValidateException(String.format("回滚的预付款%s核销金额超已核销金额%f,请检查付款单!",info.getPayment_sn(),fix.setScale(2, BigDecimal.ROUND_CEILING)));
		case 0:
			params.put("payment_status", InvoiceStatus.NOT_WRITTEN_OFF);
			break;
		case 1:
			params.put("payment_status", InvoiceStatus.PARTIAL_WRITE_OFF);
			break;
		}
		params.put("written_off_price", fix);
		mapper.updateAdvancePayment(params);
	}
}
