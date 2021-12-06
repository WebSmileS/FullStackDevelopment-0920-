package com.yrt.project.api.business.purchase;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.yrt.common.dict.RefVoucherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.DBException;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.Utils;
import com.yrt.project.api.business.purchase.vo.AddPurchaseReturnForm;
import com.yrt.project.api.business.purchase.vo.EditPurchaseReturnForm;
import com.yrt.project.api.business.sale.vo.SearchReturnProductForm;
import com.yrt.project.api.business.warehouse.service.IGrnService;
import com.yrt.project.api.business.warehouse.vo.grn.AddGrnForm;
import com.yrt.project.api.business.warehouse.vo.grn.EditGrnForm;
import com.yrt.project.api.business.warehouse.vo.grn.GrnIDForm;
import com.yrt.project.api.business.warehouse.vo.grn.GrnIds;
import com.yrt.project.api.business.warehouse.vo.grn.SearchGrnForm;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.warehouse.domain.Grn;
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
public class PurchaseReturnService {

	@Autowired DealerDealerGrnMapper ddMapper;
	@Autowired DealerHospitalGrnMapper dhMapper;
	@Autowired DealerVendorGrnMapper dvMapper;
	@Autowired HospitalHospitalGrnMapper hhMapper;
	@Autowired HospitalDealerGrnMapper hdMapper;
	@Autowired HospitalVendorGrnMapper hvMapper;
	@Autowired VendorDealerGrnMapper vdMapper;
	@Autowired VendorHospitalGrnMapper vhMapper;
	@Autowired VendorVendorGrnMapper vvMapper;
	
	@Autowired IGrnService grnService;
	@Autowired VoucherNumberService numberUtils;
	
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
	
	public List<Grn> searchPurchaseReturnList(SearchGrnForm form, List<Long> warehouseIds) {
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
		Map<String, Object> params = form.getInfo();
		params.put("type_inner_sn", 1L);
		params.put("warehouseIds", warehouseIds);
		return mapper.searchGrnList(params);
	}
	
	@Transactional
	public Grn insertPurchaseReturn(OrgEmployee makingPeople, SystemType curUserSystemType, @Valid AddPurchaseReturnForm form) {
		AddGrnForm grnForm = Utils.clone(form, AddGrnForm.class);
		grnForm.setType_inner_sn(1L);
		grnForm.setType_name("采购退货");
		grnForm.setDetails(form.getDetails());
		String sn = numberUtils.getMaxSn(VoucherType.PURCHASE_RETURN, ServletUtils.getCurUserSystemType(), form.getOrg_id());
		return grnService.insertGrn(grnForm, sn, makingPeople);
	}

	@Transactional
	public Grn updatePurchaseReturn(@Valid EditPurchaseReturnForm form, OrgEmployee makingPeople) {
		EditGrnForm grnForm = Utils.clone(form, EditGrnForm.class);
		grnForm.setType_inner_sn(1L);
		grnForm.setType_name("采购退货");
		grnForm.setDetails(form.getDetails());
		return grnService.updateGrn(grnForm, makingPeople);
	}

	@Transactional
	public int deletePurchaseReturn(GrnIDForm form, OrgEmployee makingPeople) {
		return grnService.deleteGrn(form, makingPeople);
	}

	@Transactional
	public Grn purchaseReturnRed(@Valid GrnIDForm form, ContextUserInfo curUser) {
		return grnService.grnRed(form, curUser, true);
	}

	@Transactional
	public void purchaseReturnUnReview(@Valid GrnIDForm form) {
		grnService.unReview(form);
	}
	
	public Grn selectPurchaseReturnInfo(GrnIDForm form) {
		return grnService.selectGrnInfo(form);
	}

	public List<Map<String, Object>> selectProductList(SearchReturnProductForm form, List<Long> warehouseIds) {
		List<Map<String, Object>> list = grnService.getGrnProductList4Return(form, warehouseIds);
		list.stream().forEach(map->{
			Integer type = Integer.parseInt(map.get("voucher_type_c").toString());
			map.put("voucher_type_c",RefVoucherType.intToEnum(type).name());
		});
		return list;
	}

	@Transactional
	public Map<String, Object> purchaseReturnPutin(@Valid GrnIds form, ContextUserInfo curUser) {
		return grnService.purchaseReturnPutIn(form, curUser);
	}
}
