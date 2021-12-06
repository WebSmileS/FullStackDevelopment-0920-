package com.yrt.project.modular.warehouse.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.warehouse.domain.OdoNotice;

@Repository
public interface OdoNoticeMapper {

	public void insertOdoNotice(OdoNotice params);
	
	public void updateOdoNotice(Map<String, Object> params);
	
	public void deleteOdoNotice(Map<String, Object> params);
	
	public List<OdoNotice> selectOdoNoticeList(Map<String, Object> params);
	
	public OdoNotice selectOdoNoticeById(Long id);
}
