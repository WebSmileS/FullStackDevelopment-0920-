package com.yrt.project.modular.product.mapper;

import java.util.List;
import java.util.Map;

import com.yrt.project.modular.product.domain.UDIDeviceRecord;

public interface UDIDeviceRecordMapper {
	
	public void insertUDIDeviceRecord(UDIDeviceRecord params);
	
	public List<UDIDeviceRecord> selectUDIDeviceRecordList(Map<String,Object> params);

}
