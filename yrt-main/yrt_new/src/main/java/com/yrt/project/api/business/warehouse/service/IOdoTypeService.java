package com.yrt.project.api.business.warehouse.service;

import java.util.List;

import com.yrt.project.api.business.warehouse.vo.odo.AddOdoTypeForm;
import com.yrt.project.api.business.warehouse.vo.odo.EditOdoTypeForm;
import com.yrt.project.modular.warehouse.domain.OdoType;

public interface IOdoTypeService {

	public OdoType insertOdoType(AddOdoTypeForm form);
	
	public int updateOdoType(EditOdoTypeForm form);
	
	public int deleteOdoType(Long oid, Long id);
	
	public List<OdoType> selectOdoTypeList(Long oid, Integer status);
}
