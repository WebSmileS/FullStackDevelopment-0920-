package com.yrt.project.modular.warehouse.mapper;

import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface VendorWarehouseMapper extends WareHouseMapper {

	public void v_add_vendor_warehouse_model(Map<String,Object> params);
	
	public void v_add_vendor_warehouse_type(Map<String,Object> params);
}
