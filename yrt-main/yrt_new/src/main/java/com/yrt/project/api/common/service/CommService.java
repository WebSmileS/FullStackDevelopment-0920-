package com.yrt.project.api.common.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.binding.BindingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.DBException;
import com.yrt.common.utils.ServletUtils;
import com.yrt.project.api.common.vo.CommSearchProductForm;
import com.yrt.project.api.common.vo.SearchAccountForm;
import com.yrt.project.modular.common.domain.ResponseProductInfo;
import com.yrt.project.modular.common.domain.SmsTemp;
import com.yrt.project.modular.common.mapper.CommMapper;
import com.yrt.project.modular.common.mapper.DDCommMapper;
import com.yrt.project.modular.common.mapper.DHCommMapper;
import com.yrt.project.modular.common.mapper.DVCommMapper;
import com.yrt.project.modular.common.mapper.HDCommMapper;
import com.yrt.project.modular.common.mapper.HHCommMapper;
import com.yrt.project.modular.common.mapper.HVCommMapper;
import com.yrt.project.modular.common.mapper.VDCommMapper;
import com.yrt.project.modular.common.mapper.VHCommMapper;
import com.yrt.project.modular.common.mapper.VVCommMapper;
import com.yrt.project.modular.platform.mapper.ProceduresMapper;
import com.yrt.project.modular.product.mapper.DealerProductUnitMapper;
import com.yrt.project.modular.product.mapper.ProductUnitMapper;
import com.yrt.project.modular.warehouse.mapper.DealerWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.VendorWarehouseMapper;
import com.yrt.project.modular.warehouse.mapper.WareHouseMapper;

@Service
public class CommService {

	@Autowired DDCommMapper ddcommMapper;
	@Autowired DVCommMapper dvcommMapper;
	@Autowired DHCommMapper dhcommMapper;
	@Autowired HDCommMapper hdcommMapper;
	@Autowired HHCommMapper hhcommMapper;
	@Autowired HVCommMapper hvcommMapper;
	@Autowired VDCommMapper vdcommMapper;
	@Autowired VHCommMapper vhcommMapper;
	@Autowired VVCommMapper vvcommMapper;
	
	@Autowired DealerWarehouseMapper dwMapper;
	@Autowired HospitalWarehouseMapper hwMapper;
	@Autowired VendorWarehouseMapper vwMapper;
	
	@Autowired ProductUnitMapper pMapper;
	@Autowired DealerProductUnitMapper dpMapper;
	
	@Autowired ProceduresMapper proceduresMapper;
	
	private WareHouseMapper getWarehouseMapper() {
		SystemType type = ServletUtils.getCurUserSystemType();
		switch (type) {
		case HOSPITAL:
			return hwMapper;
		case DEALER:
			return dwMapper;
		case VENDOR:
			return vwMapper;
		default:
		}
		return null;
	}
	
	public CommMapper getMapper(BillsType grn_type) {
		switch (grn_type) {
		case DD:
			return ddcommMapper;
		case DV:
			return dvcommMapper;
		case DH:
			return dhcommMapper;
		case HH:
			return hhcommMapper;
		case HD:
			return hdcommMapper;
		case HV:
			return hvcommMapper;
		case VD:
			return vdcommMapper;
		case VH:
			return vhcommMapper;
		case VV:
			return vvcommMapper;
		}
		throw new DBException("当前机构不支持该功能!");
	}
	
	public List<Map<String,Object>> selectRelatedOrg4DropDownList(SystemType systemType, Long oid, String name, Boolean all, Boolean isPurchase, Boolean isOrder) {
		Map<String,Object> params = new HashMap<>();
		params.put("id", oid);
		params.put("name", name);
		if(isOrder) {
			if(isPurchase) {
				params.put("isPurchase", true);
			}else {
				params.put("isSale", true);
			}
		}else {
			params.put("isPurchase", true);
			params.put("isSale", true);
		}
		SystemType curSystemType = ServletUtils.getCurUserSystemType();
		if(!all) {
			if(!isOrder) {
				params.put("id", null);
			}
			if(curSystemType.equals(SystemType.DEALER)) {
				params.put("id", null);
			}
			switch (curSystemType) {
			case HOSPITAL://医院
				switch (systemType) {
				case HOSPITAL://HospitalHospital
					return getMapper(BillsType.HH).selectAllOrg4DropDownList(params);
				case DEALER://HospitalDealer
					return getMapper(BillsType.HD).selectAllOrg4DropDownList(params);
				case VENDOR://HospitalVendor
					return getMapper(BillsType.HV).selectAllOrg4DropDownList(params);
				default:
				}
				break;
			case DEALER://经销商
				switch (systemType) {
				case HOSPITAL://DealerHospital
					return getMapper(BillsType.DH).selectAllOrg4DropDownList(params);
				case DEALER://DealerDealer
					return getMapper(BillsType.DD).selectAllOrg4DropDownList(params);
				case VENDOR://DealerVendor
					return getMapper(BillsType.DV).selectAllOrg4DropDownList(params);
				default:
				}
				break;
			case VENDOR:
				switch(systemType){
				case HOSPITAL:
					return getMapper(BillsType.VH).selectAllOrg4DropDownList(params);
				case DEALER:
					return getMapper(BillsType.VD).selectAllOrg4DropDownList(params);
				case VENDOR:
					return getMapper(BillsType.VV).selectAllOrg4DropDownList(params);
				default:
				}
				break;
			default:
			}
		}else {
			switch (curSystemType) {
			case HOSPITAL://医院
				switch (systemType) {
				case HOSPITAL://HospitalHospital
					return getMapper(BillsType.HH).selectRelatedOrg4DropDownList(params);
				case DEALER://HospitalDealer
					return getMapper(BillsType.HD).selectRelatedOrg4DropDownList(params);
				case VENDOR://HospitalVendor
					return getMapper(BillsType.HV).selectRelatedOrg4DropDownList(params);
				default:
				}
				break;
			case DEALER://经销商
				switch (systemType) {
				case HOSPITAL://DealerHospital
					return getMapper(BillsType.DH).selectRelatedOrg4DropDownList(params);
				case DEALER://DealerDealer
					return getMapper(BillsType.DD).selectRelatedOrg4DropDownList(params);
				case VENDOR://DealerVendor
					return getMapper(BillsType.DV).selectRelatedOrg4DropDownList(params);
				default:
				}
				break;
			case VENDOR://厂商
				switch (systemType) {
				case HOSPITAL://VendorHospital
					return getMapper(BillsType.VH).selectRelatedOrg4DropDownList(params);
				case DEALER://VendorDealer
					return getMapper(BillsType.VD).selectRelatedOrg4DropDownList(params);
				case VENDOR:
					return getMapper(BillsType.VV).selectAllOrg4DropDownList(params);
				default:
				}
				break;
			default:
			}
		}
		
		return new ArrayList<Map<String, Object>>();
	}
	
	public List<ResponseProductInfo> selectVendorProductList(CommSearchProductForm form) {
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("type_inner_sn", form.getType_inner_sn());
		params.put("code68_sn", form.getCode68_sn());
		params.put("product_name", form.getName());
		params.put("vendor_inner_sn", form.getVendor_inner_sn());
		params.put("specification", form.getSpecification());
		params.put("specification_vendor_sn", form.getSpecification_vendor_sn());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());
		if(form.getBillsType().equals(BillsType.DD) && !form.getIsPurchase()) {
			params.put("organization_inner_sn", form.getOrg_id());
			params.put("org_id", form.getOrganization_inner_sn());
		}
		BillsType type = form.getBillsType();
		if((!type.equals(BillsType.HH) && !type.equals(BillsType.VV)) && form.getOnlyContrctProduct()) {
			CommMapper mapper = getMapper(form.getBillsType());
			//检查是否有合同存在，如果有合同就返回合同内产品
			return mapper.selectContractVendorProductList(params);
		}else {
			//放开对厂商只能销售自己产品的限制
//			if(type.equals(BillsType.DV) || type.equals(BillsType.HV)) {
//				params.put("vendor_inner_sn", form.getOrganization_inner_sn());
//			}
			SystemType systemType = ServletUtils.getCurUserSystemType();
			switch (systemType) {
			case HOSPITAL:
			case DEALER:
				return getMapper(BillsType.DD).selectVendorProductList(params);
			case VENDOR:
//				params.put("vendor_inner_sn", form.getOrg_id());放开对厂商只能销售自己产品的限制
				return getMapper(BillsType.VV).selectVendorProductList(params);
			default:
			}
			return new ArrayList<ResponseProductInfo>();
		}
	}
	
	public List<ResponseProductInfo> selectDealerProductList(CommSearchProductForm form, boolean isOrder) {
		BillsType type = form.getBillsType();
		if(type.equals(BillsType.DV) || type.equals(BillsType.HV) || type.equals(BillsType.VD) || type.equals(BillsType.VH) || type.equals(BillsType.VV)) {
//			throw new ValidateException("当前单据类型不支持获取经销商自建产品");
			return new ArrayList<ResponseProductInfo>();
		}
		Map<String,Object> params = new HashMap<>();
		params.put("ids", form.getOrg_id());
		params.put("name", form.getName());
		params.put("code68_sn", form.getCode68_sn());
		params.put("organization_inner_sn", form.getOrganization_inner_sn());
		params.put("dealer_inner_sn", form.getDealer_inner_sn());
		if(isOrder) {
			if(form.getIsPurchase()) {
				params.put("isPurchase", true);
			}else {
				params.put("isSale", true);
			}
		}else {
			params.put("isPurchase", true);
			params.put("isSale", true);
		}
		if(!type.equals(BillsType.HH) && form.getOnlyContrctProduct()) {
			try {
				//检查是否有合同存在，如果有合同就返回合同内产品
				CommMapper mapper = getMapper(form.getBillsType());
				return mapper.selectContractDealerProductList(params);
			} catch(BindingException e) {
				return new ArrayList<>();
			}
		}else {
			if(type.equals(BillsType.DD)) {
				if(isOrder) {
					if(form.getIsPurchase()) {
						params.put("ids", form.getOrganization_inner_sn());
					}else {
						params.put("ids", form.getOrg_id());
					}
				}else {
					params.put("ids", String.format("%d,%d", form.getOrg_id(), form.getOrganization_inner_sn()));
				}
			}else if(type.equals(BillsType.HD)) {
				params.put("ids", form.getOrganization_inner_sn());
			}else if(type.equals(BillsType.DH)) {
				params.put("ids", form.getOrg_id());
			}else if(type.equals(BillsType.HH)) {
				params.put("ids", null);
			}
			SystemType systemType = ServletUtils.getCurUserSystemType();
			switch (systemType) {
			case HOSPITAL:
				return getMapper(BillsType.DD).selectDealerProductList(params);
			case DEALER:
				return getMapper(BillsType.DD).selectDealerProductList(params);
			case VENDOR:
			default:
			}
			return new ArrayList<ResponseProductInfo>();
		}
	}
	
	public List<Map<String,Object>> getWarehouseEmployeeList(SystemType systemType, Long oid, Long wid, Integer warehouseEmployeeType, Boolean includeChildWarehouseEmployee) {
		CommMapper mapper = null;
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", oid);
		params.put("warehouse_model_inner_sn", wid);
		params.put("type", warehouseEmployeeType);
		switch (systemType) {
		case HOSPITAL:
			mapper = getMapper(BillsType.HH);
			break;
		case DEALER:
			mapper = getMapper(BillsType.DD);
			break;
		case VENDOR:
			mapper = getMapper(BillsType.VV);
			break;
		default:
			return new ArrayList<Map<String, Object>>();
		}
		if(includeChildWarehouseEmployee) {
			WareHouseMapper whMapper = getWarehouseMapper();
			String wIds = whMapper.selectAllChildrenWarehouseModeIDS(params);
			params.put("warehouse_model_inner_sn", wIds);
		}
		return mapper.selectWarehouseEmployeeList(params);
	}
	
	public BigDecimal getProductUnitMeasure(Long vid, Long pid, Long psid, Long puid, int productType) {
		Map<String,Object> params = new HashMap<>();
		params.put("vendor_inner_sn_i", vid);
		params.put("product_inner_sn_i", pid);
		params.put("specification_inner_sn_i", psid);
		params.put("unit_inner_sn_i", puid);
		if(productType == 1) {
			return pMapper.calculate_product_min_unit(params);
		}else {
			return dpMapper.calculate_product_min_unit(params);
		}
	}
	
	public Long getProductMinUnitId(Long vid, Long pid, Long psid, Long puid, int productType) {
		Map<String,Object> params = new HashMap<>();
		params.put("vendor_inner_sn_i", vid);
		params.put("product_inner_sn_i", pid);
		params.put("specification_inner_sn_i", psid);
		params.put("unit_inner_sn_i", puid);
		if(productType == 1) {
			return pMapper.c_get_product_min_unit_inner_sn(params);
		}else {
			return dpMapper.c_get_dealer_product_min_unit_inner_sn(params);
		}
	}
	
	public String getProductMinUnitName(Long vid, Long pid, Long psid, Long puid, int productType) {
		Map<String,Object> params = new HashMap<>();
		params.put("vendor_inner_sn_i", vid);
		params.put("product_inner_sn_i", pid);
		params.put("specification_inner_sn_i", psid);
		params.put("unit_inner_sn_i", puid);
		if(productType == 1) {
			return pMapper.c_get_product_min_unit(params);
		}else {
			return dpMapper.c_get_dealer_product_min_unit(params);
		}
	}

	public List<SmsTemp> getInitLoginAccount(SearchAccountForm form) {
		Map<String,Object> params = new HashMap<>();
		params.put("organization_name", form.getOrganization_name());
		params.put("account", form.getAccount());
		params.put("type", form.getType());
		params.put("begin_date", form.getBegin_date());
		params.put("end_date", form.getEnd_date());
		return proceduresMapper.selectInitLoginAccount(params);
	}
}
