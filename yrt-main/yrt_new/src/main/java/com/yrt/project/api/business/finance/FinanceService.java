package com.yrt.project.api.business.finance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yrt.common.dict.SystemType;
import com.yrt.project.api.business.finance.vo.SearchInventoryForm;
import com.yrt.project.api.business.finance.vo.SearchRecordForm;
import com.yrt.project.modular.finance.domain.GrnRecord;
import com.yrt.project.modular.finance.domain.InventoryRecord;
import com.yrt.project.modular.finance.domain.OdoRecord;
import com.yrt.project.modular.finance.mapper.GrnRecordMapper;
import com.yrt.project.modular.finance.mapper.InventoryRecordMapper;
import com.yrt.project.modular.finance.mapper.OdoRecordMapper;

@Service
public class FinanceService {

	@Autowired GrnRecordMapper grnMapper;
	@Autowired OdoRecordMapper odoMapper;
	@Autowired InventoryRecordMapper invMapper;
	
	private Map<String,Object> init(SearchRecordForm form){
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("warehouse_model_inner_sn", form.getWarehouse_model_inner_sn());
		params.put("sn", form.getSn());
		params.put("handler_name", form.getHandler_name());
		params.put("organization_name", form.getOrganization_name());
		params.put("product_name", form.getProduct_name());
		params.put("begin_date", form.getBegin_date());
		params.put("end_date", form.getEnd_date());
		params.put("product_vendor_inner_sn", form.getProduct_vendor_inner_sn());
		params.put("product_dealer_inner_sn", form.getProduct_dealer_inner_sn());
		params.put("department_inner_sn", form.getDepartment_inner_sn());
		if(form.getProduct_vendor_inner_sn() == null && form.getProduct_dealer_inner_sn() == null) {
			params.put("selectVendor", 1);
			params.put("selectDealer", 1);
		}else {
			if(form.getProduct_vendor_inner_sn() == null) {
				params.put("selectDealer", 1);
			}else {
				params.put("selectVendor", 1);
			}
		}
		return params;
	}
	
	public List<GrnRecord> getGrnRecordList(SystemType systemType, SearchRecordForm form){
		Map<String,Object> params = init(form);
		
		switch (systemType) {
		case DEALER:
			return grnMapper.selectDealerGrnRecordList(params);
		case HOSPITAL:
			return grnMapper.selectHospitalGrnRecordList(params);
		case VENDOR:
			return grnMapper.selectVendorGrnRecordList(params);
		default:
			break;
		}
		return new ArrayList<>();
	}
	
	public BigDecimal sumGrnRecordList(SystemType systemType, SearchRecordForm form) {
		Map<String,Object> params = init(form);
		
		switch (systemType) {
		case DEALER:
			return grnMapper.sumDealerGrnRecordList(params);
		case HOSPITAL:
			return grnMapper.sumHospitalGrnRecordList(params);
		case VENDOR:
			return grnMapper.sumVendorGrnRecordList(params);
		default:
			break;
		}
		return BigDecimal.ZERO;
	}
	
	public List<OdoRecord> getOdoRecordList(SystemType systemType, SearchRecordForm form){
		Map<String,Object> params = init(form);
		
		switch (systemType) {
		case DEALER:
			return odoMapper.selectDealerOdoRecordList(params);
		case HOSPITAL:
			return odoMapper.selectHospitalOdoRecordList(params);
		case VENDOR:
			if(form.getProduct_dealer_inner_sn() != null) {
				return new ArrayList<>();
			}
			return odoMapper.selectVendorOdoRecordList(params);
		default:
			break;
		}
		return new ArrayList<>();
	}
	
	public BigDecimal sumOdoRecordList(SystemType systemType, SearchRecordForm form){
		Map<String,Object> params = init(form);
		
		switch (systemType) {
		case DEALER:
			return odoMapper.sumDealerOdoRecordList(params);
		case HOSPITAL:
			return odoMapper.sumtHospitalOdoRecordList(params);
		case VENDOR:
			if(form.getProduct_dealer_inner_sn() != null) {
				return BigDecimal.ZERO;
			}
			return odoMapper.sumVendorOdoRecordList(params);
		default:
			break;
		}
		return BigDecimal.ZERO;
	}

	public List<InventoryRecord> getInventoryRecordList(SystemType systemType, SearchInventoryForm form) {
		Map<String,Object> params = new HashMap<>();
		params.put("org_id", form.getOrg_id());
		params.put("warehouse_inner_sn", form.getWarehouse_model_inner_sn());
		params.put("product_name", form.getProduct_name());
		params.put("employee_inner_sn", form.getEmployee_inner_sn());
		params.put("orderByColumn", form.getOrderByColumn());
		
		switch (systemType) {
		case DEALER:
			return invMapper.selectDealerInventoryProductList(params);
		case HOSPITAL:
			return invMapper.selectHospitalInventoryProductList(params);
		case VENDOR:
			return invMapper.selectVendorInventoryProductList(params);
		default:
			break;
		}
		return new ArrayList<>();
	}
}
