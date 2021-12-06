package com.yrt.project.modular.warehouse.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.warehouse.domain.OdoType;

@Repository
public interface OdoTypeMapper {

	//出库单类型
	public void insertOdoType(Map<String,Object> params);
	public int updateOdoType(Map<String,Object> params);
	public int deleteOdoType(Map<String,Object> params);
	public List<OdoType> selectOdoTypeList(Map<String,Object> params);
	public OdoType selectOdoTypeInfo(Map<String,Object> params);
	
	public List<Map<String,Object>> checkUseList(Map<String,Object> params);
}
