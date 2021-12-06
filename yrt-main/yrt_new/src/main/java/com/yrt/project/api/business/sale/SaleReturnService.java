package com.yrt.project.api.business.sale;

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
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.ContextUserInfo;
import com.yrt.common.utils.ServletUtils;
import com.yrt.common.utils.Utils;
import com.yrt.project.api.business.sale.vo.AddSaleReturnForm;
import com.yrt.project.api.business.sale.vo.EditSaleReturnForm;
import com.yrt.project.api.business.sale.vo.SearchReturnProductForm;
import com.yrt.project.api.business.warehouse.service.IOdoService;
import com.yrt.project.api.business.warehouse.vo.odo.AddOdoForm;
import com.yrt.project.api.business.warehouse.vo.odo.EditOdoForm;
import com.yrt.project.api.business.warehouse.vo.odo.OdoIDForm;
import com.yrt.project.api.business.warehouse.vo.odo.OdoListIDForm;
import com.yrt.project.api.business.warehouse.vo.odo.SearchOdoForm;
import com.yrt.project.api.common.service.VoucherNumberService;
import com.yrt.project.modular.organization.domain.OrgEmployee;
import com.yrt.project.modular.warehouse.domain.Odo;
import com.yrt.project.modular.warehouse.mapper.DealerDealerOdoMapper;
import com.yrt.project.modular.warehouse.mapper.DealerHospitalOdoMapper;
import com.yrt.project.modular.warehouse.mapper.DealerVendorOdoMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalDealerOdoMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalHospitalOdoMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalVendorOdoMapper;
import com.yrt.project.modular.warehouse.mapper.OdoMapper;
import com.yrt.project.modular.warehouse.mapper.VendorDealerOdoMapper;
import com.yrt.project.modular.warehouse.mapper.VendorHospitalOdoMapper;

@Service
public class SaleReturnService {

	@Autowired DealerDealerOdoMapper ddMapper;
	@Autowired DealerHospitalOdoMapper dhMapper;
	@Autowired DealerVendorOdoMapper dvMapper;
	@Autowired HospitalHospitalOdoMapper hhMapper;
	@Autowired HospitalDealerOdoMapper hdMapper;
	@Autowired HospitalVendorOdoMapper hvMapper;
	@Autowired VendorDealerOdoMapper vdMapper;
	@Autowired VendorHospitalOdoMapper vhMapper;
	
	@Autowired IOdoService odoService;
//	@Autowired IGrnService grnService;
	@Autowired VoucherNumberService numberUtils;

	/**
	 * 出库单类型 0-DealerDealer 1-DealerVendor 2-DealerHospital 3-HospitalHospital
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
		throw new DBException("当前机构不支持该功能!");
	}
	
	public List<Odo> searchSaleReturnList(SearchOdoForm form, List<Long> warehouseIds) {
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
		Map<String,Object> params = form.getInfo();
		params.put("type_inner_sn", 1);
		params.put("warehouseIds", warehouseIds);
		return mapper.searchOdoList(params);
	}
	
	@Transactional
	public Odo insertSaleReturn(AddSaleReturnForm form, SystemType curUserSystemType, OrgEmployee makingPeople) {
		AddOdoForm odoForm = Utils.clone(form, AddOdoForm.class);
		odoForm.setDetails(form.getDetails());
		odoForm.setType_inner_sn(1L);
		odoForm.setType_name("销售退货");
		String sn = numberUtils.getMaxSn(VoucherType.SALE_RETURN, ServletUtils.getCurUserSystemType(), form.getOrg_id());
		return odoService.insertOdoAndCheckOverVoucher(makingPeople, curUserSystemType, odoForm, sn, true);
	}

	@Transactional
	public Odo updateSaleReturn(EditSaleReturnForm form, OrgEmployee makingPeople) {
		EditOdoForm odoForm = Utils.clone(form, EditOdoForm.class);
		odoForm.setDetails(form.getDetails());
		odoForm.setType_inner_sn(1L);
		odoForm.setType_name("销售退货");
		return odoService.updateOdo(odoForm, makingPeople, true);
	}

	@Transactional
	public int deleteSaleReturn(OdoIDForm form) {
		return odoService.deleteOdo(form, true);
	}

	@Transactional
	public Odo saleReturnRed(@Valid OdoIDForm form, ContextUserInfo curUser) {
		return odoService.odoRed(form, curUser);
	}

	@Transactional
	public void saleReturnUnReview(@Valid OdoIDForm form) {
		odoService.unReview(form);
	}
	
	@Transactional
	public String saleReturnPutout(OdoListIDForm form, ContextUserInfo curUser) {
		return odoService.putout(form, curUser, true);
	}

	@Transactional
	public void printed(OdoIDForm form) {
		odoService.printed(form);
	}
	
	public Odo selectSaleReturnInfo(OdoIDForm form) {
		return odoService.selectOdoInfo(form);
	}

	public List<Map<String, Object>> selectProductList(SearchReturnProductForm form, List<Long> warehouseIds) {
		if(BillsType.VV.equals(form.getBillsType())) {
			throw new ValidateException("厂商与厂商之间暂时不支持销售退货!");
		}
		List<Map<String, Object>> list = odoService.getOdoProductList4Return(form, warehouseIds);
		list.stream().forEach(map->{
			Integer type = Integer.parseInt(map.get("voucher_type_c").toString());
			map.put("voucher_type_c", RefVoucherType.intToEnum(type).name());
		});
		return list;
	}
	
}
