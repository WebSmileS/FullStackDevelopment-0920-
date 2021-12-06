package com.yrt.project.api.business.purchase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.RefVoucherType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.DBException;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.Utils;
import com.yrt.project.api.business.order.service.PurchasingRecordService;
import com.yrt.project.api.business.purchase.vo.AddPurchaseReturnForm;
import com.yrt.project.api.business.purchase.vo.EditPurchaseReturnForm;
import com.yrt.project.api.business.warehouse.service.IGrnService;
import com.yrt.project.api.business.warehouse.vo.grn.AddGrnForm;
import com.yrt.project.api.business.warehouse.vo.grn.EditGrnForm;
import com.yrt.project.api.business.warehouse.vo.grn.GrnDetailForm;
import com.yrt.project.api.business.warehouse.vo.grn.GrnIDForm;
import com.yrt.project.api.business.warehouse.vo.grn.SearchGrnForm;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.modular.order.domain.PurchasingRecord;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.warehouse.domain.Grn;
import com.yrt.project.modular.warehouse.domain.InspectionRecord;
import com.yrt.project.modular.warehouse.domain.InspectionRecordDetail;
import com.yrt.project.modular.warehouse.domain.TransferVoucher;
import com.yrt.project.modular.warehouse.domain.TransferVoucherDetail;
import com.yrt.project.modular.warehouse.mapper.DealerDealerGrnMapper;
import com.yrt.project.modular.warehouse.mapper.DealerHospitalGrnMapper;
import com.yrt.project.modular.warehouse.mapper.DealerVendorGrnMapper;
import com.yrt.project.modular.warehouse.mapper.GrnMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalDealerGrnMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalHospitalGrnMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalVendorGrnMapper;
import com.yrt.project.modular.warehouse.mapper.VendorDealerGrnMapper;
import com.yrt.project.modular.warehouse.mapper.VendorHospitalGrnMapper;
import com.yrt.project.modular.warehouse.mapper.VendorVendorGrnMapper;

@Service
public class PurchaseGrnService {

	@Autowired private DealerDealerGrnMapper ddMapper;
	@Autowired private DealerHospitalGrnMapper dhMapper;
	@Autowired private DealerVendorGrnMapper dvMapper;
	@Autowired private HospitalHospitalGrnMapper hhMapper;
	@Autowired private HospitalDealerGrnMapper hdMapper;
	@Autowired private HospitalVendorGrnMapper hvMapper;
	@Autowired private VendorDealerGrnMapper vdMapper;
	@Autowired private VendorHospitalGrnMapper vhMapper;
	@Autowired private VendorVendorGrnMapper vvMapper;
	
	@Autowired IGrnService grnService;
	@Autowired VoucherNumberService numberUtils;
	@Autowired PurchasingRecordService recordService;

	private GrnMapper getMapper(BillsType billsType) {
		switch (billsType) {
		case DD:
			return ddMapper;
		case DV:
			return dvMapper;
		case DH:
			return dhMapper;
		case HH:
			return hhMapper;
		case HD:
			return hdMapper;
		case HV:
			return hvMapper;
		case VD:
			return vdMapper;
		case VH:
			return vhMapper;
		case VV:
			return vvMapper;
		}
		throw new DBException("当前机构不支持该功能!");
	}
	
	@Transactional
	public void autoCreatePurchaseGrn(InspectionRecord entity, ContextUserInfo curUser) {
		AddGrnForm grnForm = Utils.clone(entity, AddGrnForm.class);
		grnForm.setType_inner_sn(0L);
		grnForm.setType_name("采购入库");
		grnForm.setBillsType(entity.getBills_type());
		grnForm.setDepartment_inner_sn(curUser.getDeptList().get(0).getDepartment_inner_sn());
		grnForm.setDepartment_name(curUser.getDeptList().get(0).getName());
		grnForm.setHandler_inner_sn(curUser.getEmployee().getEmployee_inner_sn());
		grnForm.setHandler_name(curUser.getEmployee().getName());
		grnForm.setDescription(Utils.isEmpty(entity.getDescription())?"":entity.getDescription());
		grnForm.setStatus(0);
		List<GrnDetailForm> grnDetails = new ArrayList<>();
		List<InspectionRecordDetail> details = entity.getDetails();
		StringBuffer sb = new StringBuffer();
		List<Long> buyerList = new ArrayList<>();
		int i = 0;
		for (InspectionRecordDetail detail : details) {
			PurchasingRecord record = recordService.getPurchasingRecordInfo(detail.getVoucher_inner_sn_c());
			GrnDetailForm form = Utils.clone(detail, GrnDetailForm.class);
			form.setAction(1);
			form.setInspection_detail_id(detail.getId());
			form.setUnit_name(detail.getUnit());
			form.setQuantity(detail.getQualified_quantity());
			form.setQuantity_old(detail.getQualified_quantity());
			form.setRate(BigDecimal.ZERO);
			form.setUnit_price(record.getUnit_price());
			form.setSort_number(i);
			form.setRate(record.getRate());
			form.setVoucher_type_c(RefVoucherType.PO_RECORDS);
			form.setVoucher_inner_sn_c(detail.getVoucher_inner_sn_c());
			form.setVoucher_detail_inner_sn_c(detail.getVoucher_inner_sn_c());
			form.setType(0);
			if (!buyerList.contains(record.getBuyer_inner_sn())) {
				buyerList.add(record.getBuyer_inner_sn());
				if(sb.length() == 0) {
					sb.append(record.getBuyer_name());
				}else {
					sb.append(",");
					sb.append(record.getBuyer_name());
				}
			}
			i++;
			grnDetails.add(form);
		}
		grnForm.setBuyer_name(sb.toString());
		grnForm.setDetails(grnDetails);
		String sn = numberUtils.getMaxSn(VoucherType.PUT_IN, entity.getSystem_type(), entity.getOrg_id());
		
		grnService.insertGrn(grnForm, sn, curUser.getEmployee());
	}
	
	@Transactional
	public Grn insertPurchaseGrn(AddPurchaseReturnForm form, OrgEmployee makingPeople) {
		AddGrnForm grnForm = Utils.clone(form, AddGrnForm.class);
		grnForm.setReceiving_name(form.getAuditor_name());
		grnForm.setType_inner_sn(0L);
		grnForm.setType_name("采购入库");
		grnForm.setDetails(form.getDetails());
		String sn = numberUtils.getMaxSn(VoucherType.PUT_IN, ServletUtils.getCurUserSystemType(), form.getOrg_id());
		return grnService.insertGrn(grnForm, sn, makingPeople);
	}

	@Transactional
	public Grn updatePurchaseGrn(EditPurchaseReturnForm form, OrgEmployee makingPeople) {
		EditGrnForm grnForm = Utils.clone(form, EditGrnForm.class);
		grnForm.setType_inner_sn(0L);
		grnForm.setType_name("采购入库");
		grnForm.setDetails(form.getDetails());
		return grnService.updateGrn(grnForm, makingPeople);
	}

	@Transactional
	public int deletePurchaseGrn(GrnIDForm form, OrgEmployee makingPeople) {
		return grnService.deleteGrn(form, makingPeople);
	}

	public List<Grn> searchPurchaseGrnList(SearchGrnForm form, List<Long> warehouseIds) {
		GrnMapper mapper = null;
		SystemType systemType = ServletUtils.getCurUserSystemType();
		switch (systemType) {
		case HOSPITAL:
			mapper = getMapper(BillsType.HH);
			break;
		case DEALER:
			mapper = getMapper(BillsType.DD);
			break;
		case VENDOR:
			mapper = getMapper(BillsType.VD);
			break;
		default:
		}
		Map<String,Object> params = form.getInfo();
		params.put("type_inner_sn", 0L);
		params.put("warehouseIds", warehouseIds);
		return mapper.searchGrnList(params);
	}

	@Transactional
	public Grn purchaseGrnRed(@Valid GrnIDForm form, ContextUserInfo curUser) {
		return grnService.grnRed(form, curUser, false);
	}

	@Transactional
	public void purchaseGrnUnReview(@Valid GrnIDForm form) {
		grnService.unReview(form);
	}

	public Grn selectPurchaseGrnInfo(GrnIDForm form) {
		return grnService.selectGrnInfo(form);
	}

	public List<TransferVoucher> getTransferVoucherList(SystemType systemType, long orgId, String sn, String organizationName,
			String warehouse_name, RefVoucherType voucherType, String key) {
		return grnService.getTransferVoucherList(systemType, orgId, sn, organizationName, warehouse_name, voucherType, key);
	}

	public List<TransferVoucherDetail> getTransferVoucherDetailList(long orgId, Long vId, Long organization_inner_sn, BillsType billsType,
			RefVoucherType voucherType) {
		return grnService.getTransferVoucherDetailList(orgId, vId, organization_inner_sn, billsType, voucherType);
	}

	public void updatePurchaseGrnDetailInspectionDetailId(Long org_id, Long grn_id, Long grn_detail_id, Long inspectionDetailId, BillsType billsType) {
		grnService.updatePurchaseGrnDetailInspectionDetailId(org_id, grn_id, grn_detail_id, inspectionDetailId, billsType);
	}
}
