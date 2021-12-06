package com.yrt.project.api.business.warehouse.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;
import com.yrt.common.exception.DBException;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.Utils;
import com.yrt.project.api.business.purchase.vo.AddPurchaseReturnForm;
import com.yrt.project.api.business.purchase.vo.EditPurchaseReturnForm;
import com.yrt.project.api.business.warehouse.vo.grn.AddGrnForm;
import com.yrt.project.api.business.warehouse.vo.grn.EditGrnForm;
import com.yrt.project.api.business.warehouse.vo.grn.GrnIDForm;
import com.yrt.project.api.business.warehouse.vo.grn.SearchGrnForm;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.warehouse.domain.AllotVoucher;
import com.yrt.project.modular.warehouse.domain.AllotVoucherDetail;
import com.yrt.project.modular.warehouse.domain.Grn;
import com.yrt.project.modular.warehouse.mapper.AllotVoucherMapper;
import com.yrt.project.modular.warehouse.mapper.DealerAllotVoucherMapper;
import com.yrt.project.modular.warehouse.mapper.DealerDealerGrnMapper;
import com.yrt.project.modular.warehouse.mapper.DealerHospitalGrnMapper;
import com.yrt.project.modular.warehouse.mapper.DealerVendorGrnMapper;
import com.yrt.project.modular.warehouse.mapper.GrnMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalAllotVoucherMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalDealerGrnMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalHospitalGrnMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalVendorGrnMapper;
import com.yrt.project.modular.warehouse.mapper.VendorAllotVoucherMapper;
import com.yrt.project.modular.warehouse.mapper.VendorDealerGrnMapper;
import com.yrt.project.modular.warehouse.mapper.VendorHospitalGrnMapper;
import com.yrt.project.modular.warehouse.mapper.VendorVendorGrnMapper;

@Service
public class AllotGrnService {

	@Autowired private DealerDealerGrnMapper ddMapper;
	@Autowired private DealerHospitalGrnMapper dhMapper;
	@Autowired private DealerVendorGrnMapper dvMapper;
	@Autowired private HospitalHospitalGrnMapper hhMapper;
	@Autowired private HospitalDealerGrnMapper hdMapper;
	@Autowired private HospitalVendorGrnMapper hvMapper;
	@Autowired private VendorDealerGrnMapper vdMapper;
	@Autowired private VendorHospitalGrnMapper vhMapper;
	@Autowired private VendorVendorGrnMapper vvMapper;
	
	@Autowired HospitalAllotVoucherMapper haMapper;
    @Autowired DealerAllotVoucherMapper daMapper;
    @Autowired VendorAllotVoucherMapper vaMapper;
	
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
	
	private AllotVoucherMapper getAllotVoucherMapper() {
        SystemType systemType = ServletUtils.getCurUserSystemType();
        switch (systemType) {
            case DEALER:
                return daMapper;
            case VENDOR:
                return vaMapper;
            default:
                return haMapper;
        }
    }
	
	@Transactional
	public Grn insertAllotGrn(AddPurchaseReturnForm form, OrgEmployee makingPeople) {
		AddGrnForm grnForm = Utils.clone(form, AddGrnForm.class);
		grnForm.setType_inner_sn(2L);
		grnForm.setType_name("调拨入库");
		grnForm.setDetails(form.getDetails());
		String sn = numberUtils.getMaxSn(VoucherType.PUT_IN, ServletUtils.getCurUserSystemType(), form.getOrg_id());
		return grnService.insertGrn(grnForm, sn, makingPeople);
	}

	@Transactional
	public Grn updateAllotGrn(EditPurchaseReturnForm form, OrgEmployee makingPeople) {
		EditGrnForm grnForm = Utils.clone(form, EditGrnForm.class);
		grnForm.setType_inner_sn(2L);
		grnForm.setType_name("调拨入库");
		grnForm.setDetails(form.getDetails());
		return grnService.updateGrn(grnForm, makingPeople);
	}

	@Transactional
	public int deleteAllotGrn(GrnIDForm form, OrgEmployee makingPeople) {
		return grnService.deleteGrn(form, makingPeople);
	}

	public List<Grn> searchAllotGrnList(SearchGrnForm form, List<Long> warehouseIds) {
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
		params.put("type_inner_sn", 2);
		params.put("warehouseIds", warehouseIds);
		List<Grn> res = mapper.searchGrnList(params);
		return res;
	}

	@Transactional
	public Grn allotGrnRed(@Valid GrnIDForm form, ContextUserInfo curUser) {
		return grnService.grnRed(form, curUser, false);
	}

	@Transactional
	public void allotGrnUnReview(@Valid GrnIDForm form) {
		grnService.unReview(form);
	}

	public Grn selectAllotGrnInfo(GrnIDForm form) {
		return grnService.selectGrnInfo(form);
	}
	
	public List<AllotVoucher> getTransferAllotVoucherList(long orgId, String key, String key1, List<Long> warehouseIds) {
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", orgId);
        params.put("av_sn", key);
        params.put("status", 2);
        params.put("product_name",StringUtils.isNotEmpty(key1)?key1:null);
        params.put("warehouseIds", warehouseIds);
        return getAllotVoucherMapper().selectTransferAllotList(params);
    }

    public List<AllotVoucherDetail> getTransferAllotVoucherDetailList(long orgId, Long avid) {
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", orgId);
        params.put("status", 2);
        params.put("av_inner_sn", avid);
        return getAllotVoucherMapper().selectGrnTransferAllotDetailList(params);
    }
}
