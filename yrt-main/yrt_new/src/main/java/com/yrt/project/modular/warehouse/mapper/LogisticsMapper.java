package com.yrt.project.modular.warehouse.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.warehouse.domain.OdoLogisticsRecord;
import com.yrt.project.modular.warehouse.domain.OdoNotice;

@Repository
public interface LogisticsMapper {

	public void insertLogisticsRecord(Map<String,Object> params);
	
	public void updateLogisticsRecord(Map<String,Object> params);
	
	public void deleteLogisticsRecord(Map<String,Object> params);
	
	public List<OdoLogisticsRecord> selectLogisticsRecordList(Map<String,Object> params);
	
	public OdoLogisticsRecord selectLogisticsRecordById(Long id);
	
	public void insertLogisticsRefOdoNotice(Map<String,Object> params);
	
	public void deleteLogisticsRefOdoNotice(Map<String,Object> params);
	
	public List<OdoNotice> selectLogisticsRefOdoNotice(Map<String,Object> params);
}
