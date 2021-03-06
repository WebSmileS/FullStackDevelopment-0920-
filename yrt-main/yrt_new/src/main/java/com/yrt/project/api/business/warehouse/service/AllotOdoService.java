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
import com.yrt.common.exception.DBException;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.StringUtils;
import com.yrt.common.utils.Utils;
import com.yrt.project.api.business.sale.vo.AddSaleOdoForm;
import com.yrt.project.api.business.sale.vo.EditSaleOdoForm;
import com.yrt.project.api.business.warehouse.vo.odo.AddOdoForm;
import com.yrt.project.api.business.warehouse.vo.odo.EditOdoForm;
import com.yrt.project.api.business.warehouse.vo.odo.OdoIDForm;
import com.yrt.project.api.business.warehouse.vo.odo.SearchOdoForm;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.warehouse.domain.AllotVoucher;
import com.yrt.project.modular.warehouse.domain.AllotVoucherDetail;
import com.yrt.project.modular.warehouse.domain.Odo;
import com.yrt.project.modular.warehouse.mapper.AllotVoucherMapper;
import com.yrt.project.modular.warehouse.mapper.DealerAllotVoucherMapper;
import com.yrt.project.modular.warehouse.mapper.DealerDealerOdoMapper;
import com.yrt.project.modular.warehouse.mapper.DealerHospitalOdoMapper;
import com.yrt.project.modular.warehouse.mapper.DealerVendorOdoMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalAllotVoucherMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalDealerOdoMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalHospitalOdoMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalVendorOdoMapper;
import com.yrt.project.modular.warehouse.mapper.OdoMapper;
import com.yrt.project.modular.warehouse.mapper.VendorAllotVoucherMapper;
import com.yrt.project.modular.warehouse.mapper.VendorDealerOdoMapper;
import com.yrt.project.modular.warehouse.mapper.VendorHospitalOdoMapper;

@Service
public class AllotOdoService {

	@Autowired DealerDealerOdoMapper ddMapper;
	@Autowired DealerHospitalOdoMapper dhMapper;
	@Autowired DealerVendorOdoMapper dvMapper;
	@Autowired HospitalHospitalOdoMapper hhMapper;
	@Autowired HospitalDealerOdoMapper hdMapper;
	@Autowired HospitalVendorOdoMapper hvMapper;
	@Autowired VendorDealerOdoMapper vdMapper;
	@Autowired VendorHospitalOdoMapper vhMapper;
	
	@Autowired HospitalAllotVoucherMapper haMapper;
    @Autowired DealerAllotVoucherMapper daMapper;
    @Autowired VendorAllotVoucherMapper vaMapper;
    
	@Autowired private IOdoService odoService;
	
	/**
	 * ??????????????? 0-DealerDealer 1-DealerVendor 2-DealerHospital 3-HospitalHospital
	 * 4-HospitalDealer 5-HospitalVendor
	 */
	private OdoMapper getMapper(BillsType billsType) {
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
		}
		throw new DBException("??????????????????????????????!");
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
	
	public List<Odo> searchAllotOdoList(SearchOdoForm form, List<Long> warehouseIds) {
		OdoMapper mapper = null;
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
		params.put("type_inner_sn", 2L);
		params.put("warehouseIds", warehouseIds);
		return mapper.searchOdoList(params);
	}

	@Transactional
	public Odo insertAllotOdo(OrgEmployee makingPeople, SystemType curUserSystemType, @Valid AddSaleOdoForm form) {
		AddOdoForm odoForm = Utils.clone(form, AddOdoForm.class);
		odoForm.setDetails(form.getDetails());
		odoForm.setType_inner_sn(2L);
		odoForm.setType_name("????????????");
		return odoService.insertOdo(makingPeople, curUserSystemType, odoForm);
	}

	@Transactional
	public Odo updateAllotOdo(@Valid EditSaleOdoForm form, OrgEmployee makingPeople) {
		EditOdoForm odoForm = Utils.clone(form, EditOdoForm.class);
		return odoService.updateOdo(odoForm, makingPeople);
	}

	@Transactional
	public int deleteAllotOdo(OdoIDForm form) {
		return odoService.deleteOdo(form);
	}

	@Transactional
	public Odo allotOdoRed(@Valid OdoIDForm form, ContextUserInfo curUser) {
		return odoService.odoRed(form, curUser);
	}

	@Transactional
	public void allotOdoUnReview(@Valid OdoIDForm form) {
		odoService.unReview(form);
	}

	@Transactional
	public void printed(OdoIDForm form) {
		odoService.printed(form);
	}

	public Odo selectAllotOdoInfo(OdoIDForm form) {
		return odoService.selectOdoInfo(form);
	}
	
	public List<AllotVoucher> getTransferAllotVoucherList(long orgId, String key, String key1, List<Long> warehouseIds) {
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", orgId);
        params.put("av_sn", key);
        params.put("status", null);
        params.put("product_name",StringUtils.isNotEmpty(key1)?key1:null);
        params.put("warehouseIds", warehouseIds);
        return getAllotVoucherMapper().selectTransferAllotList(params);
    }

    public List<AllotVoucherDetail> getTransferAllotVoucherDetailList(long orgId, Long avid) {
        Map<String, Object> params = new HashMap<>();
        params.put("org_id", orgId);
        params.put("status", null);
        params.put("av_inner_sn", avid);
        return getAllotVoucherMapper().selectOdoTransferAllotDetailList(params);
    }
}
