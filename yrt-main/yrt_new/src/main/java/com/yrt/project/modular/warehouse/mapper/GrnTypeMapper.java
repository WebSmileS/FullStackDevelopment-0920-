package com.yrt.project.modular.warehouse.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.warehouse.domain.GrnType;

@Repository
public interface GrnTypeMapper {

	//入库单类型
	public void insertGrnType(Map<String,Object> params);
	public int updateGrnType(Map<String,Object> params);
	public int deleteGrnType(Map<String,Object> params);
	public List<GrnType> selectGrnTypeList(Map<String,Object> params);
	public GrnType selectGrnTypeInfo(Map<String,Object> params);
	
	public List<Map<String,Object>> checkUseList(Map<String,Object> params);
}
