package com.yrt.project.api.business.warehouse.service;

import java.util.List;

import com.yrt.common.dict.SystemType;
import com.yrt.project.api.business.warehouse.vo.AddWareHouseForm;
import com.yrt.project.api.business.warehouse.vo.AddWarehouseTypeForm;
import com.yrt.project.api.business.warehouse.vo.WarehouseCheckForm;
import com.yrt.project.api.business.warehouse.vo.WarehouseScanForm;
import com.yrt.project.api.business.warehouse.vo.EditWareHouseForm;
import com.yrt.project.api.business.warehouse.vo.EditWarehouseTypeForm;
import com.yrt.project.modular.warehouse.domain.WarehouseModel;
import com.yrt.project.modular.warehouse.domain.WarehouseType;

public interface IWarehouseService {
	
	public static final int WARE_HOUSE_TYPE_STORE = 0;
	public static final int WARE_HOUSE_TYPE_FLOOR = 1;
	public static final int WARE_HOUSE_TYPE_REGION = 2;
	public static final int WARE_HOUSE_TYPE_TRAY = 3;
	public static final int WARE_HOUSE_TYPE_SHELF_GROUP = 4;
	public static final int WARE_HOUSE_TYPE_SHELF = 5;
	public static final int WARE_HOUSE_TYPE_SLOTTING = 6;
	public static final int WARE_HOUSE_TYPE_LOCKER = 7;
	public static final int WARE_HOUSE_TYPE_STORAGE_SPACES = 8;
	
	public static final Long DEAFULT_WAREHOUSE_ID = 99999L;
//	public static final Long UNQUALIFIED_WAREHOUSE_ID = 99999L;
	

	public void initwarehouseMode(Long oid, SystemType type);
	
	public WarehouseModel insertWarehouseMode(Long oid, AddWareHouseForm form);
	
	public int updateWarehouseMode(Long oid, Long wid, EditWareHouseForm form);
	
	public int deleteWarehouseMode(Long oid, Long wid);
	
	public List<WarehouseModel> selectTopLevelWarehouseList(Long oid, Long status);
	public List<WarehouseModel> selectTopLevelWarehouseList(Long oid, SystemType systemType, Long status);
	
	public List<WarehouseModel> selectWarehouseListByPid(Long oid, Long wpid);
	
	public WarehouseModel selectWarehouseInfoById(Long oid, Long wid);
	
	public List<WarehouseModel> selectAllWarehouseTree(Long oid, Long status);
	
	//仓库类型
	public List<WarehouseType> selectWarehouseTypeList(Long oid, Long status);
	
	public WarehouseType insertWarehouseType(AddWarehouseTypeForm form);
	
	public int updateWarehouseType(EditWarehouseTypeForm form);
	
	public int deleteWarehouseType(Long oid, Long wtid);
	
	public int checkWarehouseSn(Long oid, WarehouseCheckForm form);

	public WarehouseModel scan(Long oid, WarehouseScanForm form);
}
