package com.yrt.project.api.business.invoice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.InvoiceStatus;
import com.yrt.common.dict.SystemInvoiceType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.CompressedIdGenerator;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.Utils;
import com.yrt.project.api.business.finance.vo.ApproveOdoInvoiceForm;
import com.yrt.project.api.business.finance.vo.SearchBusinessInvoiceForm;
import com.yrt.project.api.business.invoice.vo.AddInvoiceForm;
import com.yrt.project.api.business.invoice.vo.EditGrnInvoiceForm;
import com.yrt.project.api.business.invoice.vo.EditOdoInvoiceForm;
import com.yrt.project.api.business.invoice.vo.InvoiceDetailForm;
import com.yrt.project.api.business.invoice.vo.SearchInvoiceForm;
import com.yrt.project.api.business.invoice.vo.SearchInvoiceProductForm;
import com.yrt.project.api.business.warehouse.service.IGrnService;
import com.yrt.project.api.business.warehouse.service.IOdoService;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.modular.invoice.domain.SystemInvoice;
import com.yrt.project.modular.invoice.domain.SystemInvoiceDetail;
import com.yrt.project.modular.invoice.mapper.SystemInvoiceMapper;

@Service
public class InvoiceService {

	@Autowired SystemInvoiceMapper mapper;
	
	@Autowired VoucherNumberService numberUtils;
	
	@Autowired IGrnService grnService;
	@Autowired IOdoService odoService;

	@Transactional
	public SystemInvoice insertInvoice(AddInvoiceForm form, SystemInvoiceType invoiceType, ContextUserInfo curUser) {
		List<SystemInvoiceDetail> invoiceDetails = new ArrayList<>();
		if(BigDecimal.ZERO.compareTo(form.getInvoice_price()) > 0) {
			throw new ValidateException("??????????????????????????????0!!");
		}
		String sn = numberUtils.getMaxSn(
				SystemInvoiceType.GRN_INVOICE.equals(invoiceType) ? VoucherType.GRN_INVOICE : VoucherType.ODO_INVOICE,
						curUser.getSystemType(), curUser.getOrgId());
		SystemInvoice entity = Utils.clone(form, SystemInvoice.class);
		entity.setId(CompressedIdGenerator.generateId());
		entity.setOrg_id(curUser.getOrgId());
		entity.setSystem_type(curUser.getSystemType().value());
		entity.setSystem_invoice_type(invoiceType);
		entity.setSystem_invoice_sn(sn);
		entity.setCreated_by(curUser.getEmployee().getName());
		entity.setCreated_time(new Date());
		mapper.insertSystemInvoice(entity);
		List<InvoiceDetailForm> details = form.getDetail();
		saveInvoiceDetail(entity.getId(), details, invoiceDetails);
		entity.setDetails(invoiceDetails);
		return entity;
	}

	@Transactional
	public SystemInvoice updateGrnInvoice(EditGrnInvoiceForm form, SystemInvoiceType invoiceType, ContextUserInfo curUser) {
		checkInvoiceStatus(form.getId(), form.getInvoice_status());
		if(BigDecimal.ZERO.compareTo(form.getInvoice_price()) > 0) {
			throw new ValidateException("??????????????????????????????0!!");
		}
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
		params.put("invoice_no", form.getInvoice_no());
		params.put("invoice_date", form.getInvoice_date());
		params.put("rate", form.getRate());
		params.put("invoice_price", form.getInvoice_price());
		params.put("description", form.getDescription());
		params.put("invoice_type", form.getInvoice_type());
		params.put("invoice_status", form.getInvoice_status());
		mapper.updateSystemInvoice(params);
		params.put("system_invoice_id", form.getId());
		mapper.deteleSystemInvoiceDetail(params);
		List<InvoiceDetailForm> details = form.getDetail();
		saveInvoiceDetail(form.getId(), details, null);
		
		return selectInvoiceInfo(form.getId());
	}
	
	@Transactional
	private void saveInvoiceDetail(Long systemInvoiceId, List<InvoiceDetailForm> details, List<SystemInvoiceDetail> insertDetails) {
		for (InvoiceDetailForm detailForm : details) {
			if(BigDecimal.ZERO.compareTo(detailForm.getInvoiced_quantity()) == 0 ) {
				throw new ValidateException(String.format("??????%s:????????????????????????0!",detailForm.getProduct_name()));
			}
			//?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
			boolean isAgreement = detailForm.getInvoiced_quantity().multiply(detailForm.getQuantity()).compareTo(BigDecimal.ZERO) >= 0?true:false;
			//?????????????????????????????????
			boolean isNegative = detailForm.getInvoiced_quantity().multiply(new BigDecimal(-1)).compareTo(BigDecimal.ZERO) > 0?true:false;
			if(!isAgreement) {
				throw new ValidateException(String.format("??????%s:??????????????????????????????????????????????????????????????????????????????!",detailForm.getProduct_name()));
			}else {
				if(isNegative) {
					if(BigDecimal.ZERO.compareTo(detailForm.getInvoiced_quantity()) < 0 ) {
						throw new ValidateException(String.format("??????%s:????????????????????????0!",detailForm.getProduct_name()));
					}
				}else {
					if(BigDecimal.ZERO.compareTo(detailForm.getInvoiced_quantity()) > 0 ) {
						throw new ValidateException(String.format("??????%s:????????????????????????0!",detailForm.getProduct_name()));
					}
				}
			}
			if(detailForm.getQuantity().abs().compareTo(detailForm.getInvoiced_quantity().abs()) < 0) {
				throw new ValidateException(String.format("??????%s:????????????????????????????????????!",detailForm.getProduct_name()));
			}
			SystemInvoiceDetail detail = Utils.clone(detailForm, SystemInvoiceDetail.class);
			detail.setId(CompressedIdGenerator.generateId());
			detail.setSystem_invoice_id(systemInvoiceId);
			mapper.insertSystemInvoiceDetail(detail);
			if(insertDetails != null) {
				insertDetails.add(detail);
			}
		}
	}

	@Transactional
	public SystemInvoice updateOdoInvoice(EditOdoInvoiceForm form, SystemInvoiceType invoiceType, ContextUserInfo curUser) {
		checkInvoiceStatus(form.getId(), form.getInvoice_status());
		if(BigDecimal.ZERO.compareTo(form.getInvoice_price()) > 0) {
			throw new ValidateException("??????????????????????????????0!!");
		}
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
		params.put("invoice_no", form.getInvoice_no());
		params.put("invoice_date", form.getInvoice_date());
		params.put("rate", form.getRate());
		params.put("invoice_price", form.getInvoice_price());
		params.put("description", form.getDescription());
		params.put("invoice_type", form.getInvoice_type());
		params.put("invoice_status", form.getInvoice_status());
		mapper.updateSystemInvoice(params);
		params.put("system_invoice_id", form.getId());
		mapper.deteleSystemInvoiceDetail(params);
		List<InvoiceDetailForm> details = form.getDetail();
		for (InvoiceDetailForm detailForm : details) {
			SystemInvoiceDetail detail = Utils.clone(detailForm, SystemInvoiceDetail.class);
			detail.setId(CompressedIdGenerator.generateId());
			detail.setSystem_invoice_id(form.getId());
			mapper.insertSystemInvoiceDetail(detail);
		}
		return selectInvoiceInfo(form.getId());
	}

	private void checkInvoiceStatus(Long id, InvoiceStatus newStatus) {
		if(Utils.isEmpty(id)) {
			throw new ValidateException("????????????ID????????????!!");
		}
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		SystemInvoice info = mapper.selectSystemInvoiceInfo(params);
		if(Utils.isEmpty(info)) {
			throw new ValidateException("?????????????????????ID!!");
		}
		InvoiceStatus oldStatus = info.getInvoice_status();
		switch (oldStatus) {
		case DRAFT:
			if(!InvoiceStatus.DRAFT.equals(newStatus)
					&& !InvoiceStatus.PENDING.equals(newStatus)) {
				throw new ValidateException("???????????????????????????????????????????????????!!");
			}
			break;
		case PENDING:
			if(!InvoiceStatus.NOT_WRITTEN_OFF.equals(newStatus)) {
				throw new ValidateException("?????????????????????????????????????????????!!");
			}
			break;
		default:
			break;
		}
	}
	
	private void checkInvoiceStatus(Long id, boolean isCancel, boolean isDelete) {
		if(Utils.isEmpty(id)) {
			throw new ValidateException("????????????ID????????????!!");
		}
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		SystemInvoice info = mapper.selectSystemInvoiceInfo(params);
		if(Utils.isEmpty(info)) {
			throw new ValidateException("?????????????????????ID!!");
		}
		InvoiceStatus oldStatus = info.getInvoice_status();
		if(isCancel) {
			if(!InvoiceStatus.PENDING.equals(oldStatus) 
					&& !InvoiceStatus.NOT_WRITTEN_OFF.equals(info.getInvoice_status())) {
				throw new ValidateException("?????????????????????????????????????????????????????????!!");
			}
		}else {
			if(isDelete) {
				if(!InvoiceStatus.DRAFT.equals(oldStatus)
						&& !InvoiceStatus.PENDING.equals(oldStatus)) {
					throw new ValidateException("??????????????????????????????????????????????????????!!");
				}
			}else {
				if(!InvoiceStatus.DRAFT.equals(oldStatus)) {
					throw new ValidateException("??????????????????????????????????????????!!");
				}
			}
		}
		
	}
	
	private void checkInvoiceStatus(Long id, boolean isCancel) {
		checkInvoiceStatus(id, isCancel, false);
	}
	
	@Transactional
	public int submitInvoice(List<Long> list, SystemInvoiceType grnInvoice, ContextUserInfo curUser) {
		Map<String,Object> params = new HashMap<>();
		for (Long id : list) {
			checkInvoiceStatus(id, InvoiceStatus.PENDING);//?????????????????????ID
			params.put("id", id);
			params.put("invoice_status", InvoiceStatus.PENDING);
			mapper.updateSystemInvoice(params);
		}
		return 1;
	}
	
	@Transactional
	public int deleteInvoice(List<Long> list) {
		Map<String,Object> params = new HashMap<>();
		for (Long id : list) {
			checkInvoiceStatus(id, false, true);//?????????????????????ID
			params.put("id", id);
			params.put("system_invoice_id", id);
			mapper.deteleSystemInvoiceDetail(params);
			mapper.deteleSystemInvoice(params);
		}
		return 1;
	}

	public SystemInvoice selectInvoiceInfo(Long id) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		SystemInvoice info = mapper.selectSystemInvoiceInfo(params);
		if(Utils.isEmpty(info)) {
			throw new ValidateException("?????????????????????ID");
		}
		params.put("system_invoice_id", id);
		List<SystemInvoiceDetail> details = mapper.selectSystemInvoiceDetailList(params);
		info.setDetails(details);
		return info;
	}

	public List<SystemInvoice> searchInvoiceList(SearchInvoiceForm form, ContextUserInfo curUser, SystemInvoiceType invoiceType) {
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", curUser.getOrgId());
		params.put("system_type", curUser.getSystemType().value());
		params.put("system_invoice_type", invoiceType.value());
		params.put("bills_type", form.getBillsType());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());
		params.put("invoice_no", form.getInvoice_no());
		params.put("invoice_status", InvoiceStatus.ALL.equals(form.getInvoice_status())?"0,1,2,3,4,5":form.getInvoice_status().value());
		params.put("is_cancel", form.getIsCancel());
		params.put("begin_date", form.getBegin_date());
		params.put("end_date", form.getEnd_date());
		return mapper.selectSystemInvoiceList(params);
	}
	
	public List<SystemInvoice> searchInvoiceList(SearchBusinessInvoiceForm form, ContextUserInfo curUser, boolean isExport) {
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", curUser.getOrgId());
		params.put("system_type", curUser.getSystemType());
		params.put("system_invoice_type", form.getSystem_invoice_type() == -1?null:form.getSystem_invoice_type());
		params.put("bills_type", form.getBillsType());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());
		params.put("invoice_no", form.getInvoice_no());
		params.put("is_cancel", form.getIsCancel());
		if(isExport) {
			params.put("is_cancel", null);
			params.put("invoice_status", "2,3,4,5");
		}else {
			if(form.getIsCancel() == 0) {
				if(InvoiceStatus.NOT_WRITTEN_OFF.equals(form.getInvoice_status())) {
					params.put("invoice_status", "3,4");
				}else {
					params.put("invoice_status", form.getInvoice_status().value());
				}
			}else {
				params.put("invoice_status", "2,3,4,5");
			}
		}
		params.put("begin_date", form.getBegin_date());
		params.put("end_date", form.getEnd_date());
		return mapper.selectSystemInvoiceList(params);
	}

	public List<SystemInvoiceDetail> getInvoiceDetailList(Long id) {
		if(Utils.isEmpty(id)) {
			throw new ValidateException("????????????ID????????????!!");
		}
		Map<String,Object> params = new HashMap<>();
		params.put("id", id);
		SystemInvoice info = mapper.selectSystemInvoiceInfo(params);
		if(Utils.isEmpty(info)) {
			throw new ValidateException("?????????????????????ID!!");
		}
		params.put("system_invoice_id", id);
		return mapper.selectSystemInvoiceDetailList(params);
	}

	@Transactional
	public int approvalGrnInvoice(Long id, ContextUserInfo curUser) {
		Map<String,Object> params = new HashMap<>();
		checkInvoiceStatus(id, InvoiceStatus.NOT_WRITTEN_OFF);//?????????????????????ID
		params.put("id", id);
		params.put("invoice_status", InvoiceStatus.NOT_WRITTEN_OFF);
		params.put("confirm_by", curUser.getEmployee().getName());
		params.put("confirm_time", new Date());
		mapper.updateSystemInvoice(params);
		return 1;
	}
	
	private void checkOdoInvoiceInfo(ApproveOdoInvoiceForm form) {
		checkInvoiceStatus(form.getId(), InvoiceStatus.NOT_WRITTEN_OFF);//?????????????????????ID
		BigDecimal price = form.getInvoice_price();
		BigDecimal rate = form.getRate();
		if(price == null || rate == null ) {
			throw new ValidateException("?????????????????????????????????!!");
		}else {
			if(BigDecimal.ZERO.compareTo(price) > 0) {
				throw new ValidateException("??????????????????????????????0!!");
			}
			if(BigDecimal.ZERO.compareTo(rate) > 0) {
				throw new ValidateException("??????????????????0!!");
			}
		}
	}
	
	@Transactional
	public int approvalOdoInvoice(ApproveOdoInvoiceForm form, ContextUserInfo curUser) {
		Map<String,Object> params = new HashMap<>();
		checkOdoInvoiceInfo(form);//?????????????????????ID
		params.put("id", form.getId());
		params.put("invoice_no", form.getInvoice_no());
		params.put("invoice_date", form.getInvoice_date());
		params.put("rate", form.getRate());
		params.put("invoice_price", form.getInvoice_price());
		params.put("description", form.getDescription());
		params.put("invoice_type", form.getInvoice_type());
		params.put("invoice_status", InvoiceStatus.NOT_WRITTEN_OFF);
		params.put("confirm_by", curUser.getEmployee().getName());
		params.put("confirm_time", new Date());
		mapper.updateSystemInvoice(params);
		return 1;
	}

	@Transactional
	public int cancelInvoice(Long id, String reason, ContextUserInfo curUser) {
		Map<String,Object> params = new HashMap<>();
		checkInvoiceStatus(id, true);//?????????????????????ID
		params.put("id", id);
		params.put("is_cancel", 1);
		params.put("cancel_reason", reason);
		params.put("cancel_by", curUser.getEmployee().getName());
		params.put("cancel_time", new Date());
		mapper.updateSystemInvoice(params);
		return 1;
	}

	@Transactional
	public void updateInvoiceStatus(Long voucher_inner_sn_c, BigDecimal written_off_price) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", voucher_inner_sn_c);
		SystemInvoice info = mapper.selectSystemInvoiceInfo(params);
		BigDecimal fix = info.getInvoice_price().subtract(written_off_price);
		switch (fix.compareTo(BigDecimal.ZERO)) {
		case -1:
			throw new ValidateException(String.format("??????%s????????????%f,??????????????????",info.getSystem_invoice_sn(),fix.setScale(2, BigDecimal.ROUND_CEILING).abs()));
		case 0:
			params.put("invoice_status", InvoiceStatus.WRITTEN_OFF);
			break;
		case 1:
			params.put("invoice_status", InvoiceStatus.PARTIAL_WRITE_OFF);
			break;
		}
		if(BigDecimal.ZERO.compareTo(written_off_price) == 0) {
			params.put("invoice_status", InvoiceStatus.NOT_WRITTEN_OFF);
		}
		params.put("written_off_price", written_off_price);
		mapper.updateSystemInvoice(params);
	}
	
	@Transactional
	public void rollbackInvoiceStatus(Long voucher_inner_sn_c, BigDecimal written_off_price) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", voucher_inner_sn_c);
		SystemInvoice info = mapper.selectSystemInvoiceInfo(params);
		BigDecimal fix = info.getWritten_off_price().subtract(written_off_price);
		switch (fix.compareTo(BigDecimal.ZERO)) {
		case -1:
			throw new ValidateException(String.format("???????????????%s??????????????????????????????%f,??????????????????",info.getSystem_invoice_sn(),fix.setScale(2, BigDecimal.ROUND_CEILING).abs()));
		case 0:
			params.put("invoice_status", InvoiceStatus.NOT_WRITTEN_OFF);
			break;
		case 1:
			params.put("invoice_status", InvoiceStatus.PARTIAL_WRITE_OFF);
			break;
		}
		params.put("written_off_price", fix);
		mapper.updateSystemInvoice(params);
	}
	
	public List<Map<String, Object>> selectProductList(SearchInvoiceProductForm form, List<Long> warehouseIds,
			SystemInvoiceType invoiceType) {
		if(SystemInvoiceType.GRN_INVOICE.equals(invoiceType)) {
			return grnService.getGrnProductList4Invoice(form, warehouseIds);
		}else {
			return odoService.getOdoProductList4Invoice(form, warehouseIds);
		}
	}
}
