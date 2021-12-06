package com.yrt.project.modular.initial.mapper;

import java.util.List;
import java.util.Map;

import com.yrt.project.modular.initial.domain.ImportInventoryProduct;

public interface InitialInventoryMapper {

	public void insertInitialInventory(ImportInventoryProduct params);
	
	public void updateInitialInventory(ImportInventoryProduct params);
	
	public void deleteInitialInventory(Long id);
	
	public void cleanInitialInventory(Long orgId);
	
	public List<ImportInventoryProduct> selectInventoryProductList(Map<String,Object> params);
	
	public ImportInventoryProduct selectInventoryProduct(Map<String,Object> params);
	
}
