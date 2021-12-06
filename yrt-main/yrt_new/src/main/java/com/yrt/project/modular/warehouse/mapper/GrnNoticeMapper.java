package com.yrt.project.modular.warehouse.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.warehouse.domain.GrnNotice;

@Repository
public interface GrnNoticeMapper {

	public void insertGrnNotice(GrnNotice params);
	
	public void updateGrnNotice(Map<String, Object> params);
	
	public void deleteGrnNotice(Map<String, Object> params);
	
	public List<GrnNotice> selectGrnNoticeList(Map<String, Object> params);
}
