package com.yrt.project.modular.finance.mapper;

import java.util.List;
import java.util.Map;

import com.yrt.project.modular.finance.domain.*;

public interface InventoryRecordMapper {

	public List<InventoryRecord> selectDealerInventoryProductList(Map<String,Object> params);
	public List<InventoryRecord> selectHospitalInventoryProductList(Map<String,Object> params);
	public List<InventoryRecord> selectVendorInventoryProductList(Map<String,Object> params);
	
}
