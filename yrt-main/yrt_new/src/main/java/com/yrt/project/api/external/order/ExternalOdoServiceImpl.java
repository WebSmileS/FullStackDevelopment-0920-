package com.yrt.project.api.external.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.SystemType;
import com.yrt.common.exception.DBException;
import com.yrt.project.modular.warehouse.domain.TransferVoucher;
import com.yrt.project.modular.warehouse.domain.TransferVoucherDetail;
import com.yrt.project.modular.warehouse.mapper.DealerDealerGrnMapper;
import com.yrt.project.modular.warehouse.mapper.DealerVendorGrnMapper;
import com.yrt.project.modular.warehouse.mapper.GrnMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalDealerGrnMapper;
import com.yrt.project.modular.warehouse.mapper.HospitalVendorGrnMapper;

@Service
public class ExternalOdoServiceImpl {

	@Autowired private DealerDealerGrnMapper ddMapper;
	@Autowired private DealerVendorGrnMapper dvMapper;
	@Autowired private HospitalDealerGrnMapper hdMapper;
	@Autowired private HospitalVendorGrnMapper hvMapper;
	
	private GrnMapper getMapper(BillsType grn_type) {
		switch (grn_type) {
		case DD:
			return ddMapper;
		case DV:
			return dvMapper;
		case HD:
			return hdMapper;
		case HV:
			return hvMapper;
		default:
			throw new DBException("当前机构不支持该功能!");
		}
	}
	
	public List<TransferVoucher> getTransferOdoList(SystemType systemType, long orgId, String key) {
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", orgId);
		params.put("odo_sn", key);
		switch (systemType) {
		case HOSPITAL:
			return getMapper(BillsType.HD).selectTransferOdoList(params);
		case DEALER:
			return getMapper(BillsType.DD).selectTransferOdoList(params);
		default:
			break;
		}
		throw new DBException("当前机构不支持该功能!");
	}
	
	public List<TransferVoucherDetail> getTransferOdoDetailList(long orgId, Long odoId, BillsType grn_type) {
		GrnMapper mapper = getMapper(grn_type);
		Map<String, Object> params = new HashMap<>();
		params.put("org_id", orgId);
		params.put("odo_inner_sn", odoId);
		return mapper.selectTransferOdoDetailList(params);
	}
}
