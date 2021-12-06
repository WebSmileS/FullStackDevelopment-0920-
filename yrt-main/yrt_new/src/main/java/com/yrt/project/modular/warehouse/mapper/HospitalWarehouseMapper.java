package com.yrt.project.modular.warehouse.mapper;

import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface HospitalWarehouseMapper extends WareHouseMapper{
	
	public void h_add_hospital_warehouse_model(Map<String,Object> params);
	
	public void h_add_hospital_warehouse_type(Map<String,Object> params);
}
