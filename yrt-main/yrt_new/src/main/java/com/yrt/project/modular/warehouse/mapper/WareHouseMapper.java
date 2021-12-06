package com.yrt.project.modular.warehouse.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.yrt.project.modular.warehouse.domain.WarehouseModel;
import com.yrt.project.modular.warehouse.domain.WarehouseRelDept;
import com.yrt.project.modular.warehouse.domain.WarehouseRelEmployee;
import com.yrt.project.modular.warehouse.domain.WarehouseType;

public interface WareHouseMapper {

	public void insertWarehouseModel(Map<String,Object> params);
	
	public int updateWarehouseModel(WarehouseModel model);
	
	public int deleteWarehouseModel(WarehouseModel model);
	
	public int trueDeleteWarehouseModel(WarehouseModel model);
	
	public WarehouseModel selectWarehouseModelInfo(WarehouseModel model);
	
	public List<WarehouseModel> selectTopLevelWarehouseList(Map<String,Object> params);
	
	public List<WarehouseModel> selectWarehouseListByPid(WarehouseModel model);
	
	public int deleteWarehouseRelDept(WarehouseModel model);
	
	public int insertWarehouseRelDept(WarehouseRelDept relDept);
	
	public int deleteWarehouseRelEmployee(WarehouseModel model);
	
	public int insertWarehouseRelEmployee(WarehouseRelEmployee relEmp);
	
	public List<WarehouseType> selectWarehouseTypeList(Map<String,Object> params);
	
	public void insertWarehouseType(Map<String,Object> params);
	
	public WarehouseType selectWarehouseTypeInfo(WarehouseType type);
	
	public int updateWarehouseType(WarehouseType type);
	
	public int deleteWarehouseType(WarehouseType type);
	
	public List<WarehouseRelEmployee> selectWarehouseRelEmpList(WarehouseRelEmployee emp);
	
	public List<WarehouseModel> checkWarehouseSn(Map<String,Object> params);
	
	public WarehouseModel selectWarehouseModeInfoByBarcode(Map<String,Object> params);
	
	public String selectAllChildrenWarehouseModeIDS(Map<String,Object> params);
	
	/**
	 * 获取指定机构仓库所有产品的总数量，用于检测仓库是否还有用
	 * @param params
	 * @return
	 */
	public BigDecimal checkWarehouseQuantity(Map<String,Object> params);
	
	public int SelectWarehouseSnIndex(Map<String,Object> params);

	public List<WarehouseModel> warehouses(Map<String,Object> params);
	
	public Long existWarehouse(Map<String,Object> params);
}
