package com.yrt.project.api.business.sale;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.yrt.common.utils.ServiceTypeUtil;
import com.yrt.project.api.common.service.CommService;
import com.yrt.project.modular.inventory.domain.MinUnitInventory;
import com.yrt.project.modular.inventory.dto.ProductUniqueProperties;
import com.yrt.project.modular.inventory.service.impl.MinUnitInventoryService;
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
import com.yrt.project.api.business.sale.vo.AddSaleOdoForm;
import com.yrt.project.api.business.sale.vo.EditSaleOdoForm;
import com.yrt.project.api.business.warehouse.service.IOdoService;
import com.yrt.project.api.business.warehouse.vo.odo.AddOdoForm;
import com.yrt.project.api.business.warehouse.vo.odo.EditOdoForm;
import com.yrt.project.api.business.warehouse.vo.odo.OdoDetailForm;
import com.yrt.project.api.business.warehouse.vo.odo.OdoIDForm;
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
public class GiveOdoService {

	@Autowired DealerDealerOdoMapper ddMapper;
	@Autowired DealerHospitalOdoMapper dhMapper;
	@Autowired DealerVendorOdoMapper dvMapper;
	@Autowired HospitalHospitalOdoMapper hhMapper;
	@Autowired HospitalDealerOdoMapper hdMapper;
	@Autowired HospitalVendorOdoMapper hvMapper;
	@Autowired VendorDealerOdoMapper vdMapper;
	@Autowired VendorHospitalOdoMapper vhMapper;
	
	@Autowired private IOdoService odoService;
	@Autowired VoucherNumberService numberUtils;
	@Autowired CommService commService;
	@Autowired MinUnitInventoryService inventoryService;
	
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
	
	public List<Odo> searchGiveOdoList(SearchOdoForm form, List<Long> warehouseIds) {
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
		params.put("type_inner_sn", 5L);
		params.put("type_name", "赠送");
		params.put("warehouseIds", warehouseIds);
		return mapper.searchOdoList(params);
	}

	private List<OdoDetailForm> setPriceToZero(List<OdoDetailForm> details) {
		for (OdoDetailForm detail : details) {
			detail.setUnit_price(BigDecimal.ZERO);
		}
		return details;
	}
	
	@Transactional
	public Odo insertGiveOdo(OrgEmployee makingPeople, SystemType curUserSystemType, @Valid AddSaleOdoForm form) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		AddOdoForm odoForm = Utils.clone(form, AddOdoForm.class);
		List<OdoDetailForm> details = setPriceToZero(form.getDetails());
		odoForm.setDetails(details);
		String sn = numberUtils.getMaxSn(VoucherType.SALE_GIVE, ServletUtils.getCurUserSystemType(), form.getOrg_id());
		odoForm.setType_inner_sn(5L);
		odoForm.setType_name("赠送");
		odoForm.getDetails().forEach(e->{
			BigDecimal measure = commService.getProductUnitMeasure(e.getProduct_vendor_inner_sn(), e.getProduct_inner_sn(),
					e.getSpecification_inner_sn(), e.getUnit_inner_sn(), e.getProduct_type().intValue());

			// 查询库存中最小包装单位的价格
			ProductUniqueProperties productUniqueProperties = new ProductUniqueProperties(curUser.getOrgId(), e.getProduct_inner_sn(),
					e.getProduct_vendor_inner_sn(), e.getSpecification_inner_sn(), form.getWarehouse_inner_sn(), e.getBatch_number(), e.getProduct_name(), e.getProduct_vendor_name(), e.getSpecification(), form.getWarehouse_name());
			MinUnitInventory minUnitInventory = inventoryService.getMinUnitInventory(ServiceTypeUtil.getFirstType(form.getBillsType()), productUniqueProperties);
			e.setBuying_price(measure.multiply(new BigDecimal(minUnitInventory.getAveragePrice())).divide(new BigDecimal(10000)));
		});

		return odoService.insertOdoAndCheckOverVoucher(makingPeople, curUserSystemType, odoForm, sn, false);
	}

	@Transactional
	public Odo updateGiveOdo(@Valid EditSaleOdoForm form, OrgEmployee makingPeople) {
		ContextUserInfo curUser = ServletUtils.getCurUserInfo();
		EditOdoForm odoForm = Utils.clone(form, EditOdoForm.class);
		List<OdoDetailForm> details = setPriceToZero(form.getDetails());
		odoForm.setDetails(details);

		odoForm.getDetails().forEach(e->{
			BigDecimal measure = commService.getProductUnitMeasure(e.getProduct_vendor_inner_sn(), e.getProduct_inner_sn(),
					e.getSpecification_inner_sn(), e.getUnit_inner_sn(), e.getProduct_type().intValue());

			// 查询库存中最小包装单位的价格
			ProductUniqueProperties productUniqueProperties = new ProductUniqueProperties(curUser.getOrgId(), e.getProduct_inner_sn(),
					e.getProduct_vendor_inner_sn(), e.getSpecification_inner_sn(), form.getWarehouse_inner_sn(), e.getBatch_number(), e.getProduct_name(), e.getProduct_vendor_name(), e.getSpecification(), form.getWarehouse_name());
			MinUnitInventory minUnitInventory = inventoryService.getMinUnitInventory(ServiceTypeUtil.getFirstType(form.getBillsType()), productUniqueProperties);
			e.setBuying_price(measure.multiply(new BigDecimal(minUnitInventory.getAveragePrice())).divide(new BigDecimal(10000)));
		});

		return odoService.updateOdo(odoForm, makingPeople);
	}

	@Transactional
	public int deleteGiveOdo(OdoIDForm form) {
		return odoService.deleteOdo(form);
	}

	@Transactional
	public Odo GiveOdoRed(@Valid OdoIDForm form, ContextUserInfo curUser) {
		return odoService.odoRed(form, curUser);
	}

	@Transactional
	public void GiveOdoUnReview(@Valid OdoIDForm form) {
		odoService.unReview(form);
	}
	
	@Transactional
	public void printed(OdoIDForm form) {
		odoService.printed(form);
	}

	public Odo selectGiveOdoInfo(OdoIDForm form) {
		return odoService.selectOdoInfo(form);
	}
}
