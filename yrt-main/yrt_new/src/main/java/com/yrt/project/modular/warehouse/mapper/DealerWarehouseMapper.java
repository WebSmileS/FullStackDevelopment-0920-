package com.yrt.project.modular.warehouse.mapper;

import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface DealerWarehouseMapper extends WareHouseMapper {
	
	public void d_add_dealer_warehouse_model(Map<String,Object> params);
	
	public void d_add_dealer_warehouse_type(Map<String,Object> params);
}
