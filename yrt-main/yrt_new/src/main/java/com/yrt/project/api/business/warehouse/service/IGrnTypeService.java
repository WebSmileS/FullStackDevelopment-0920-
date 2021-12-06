package com.yrt.project.api.business.warehouse.service;

import java.util.List;

import com.yrt.project.api.business.warehouse.vo.grn.AddGrnTypeForm;
import com.yrt.project.api.business.warehouse.vo.grn.EditGrnTypeForm;
import com.yrt.project.modular.warehouse.domain.GrnType;

public interface IGrnTypeService {

	public GrnType insertGrnType(AddGrnTypeForm form);
	
	public int updateGrnType(EditGrnTypeForm form);
	
	public int deleteGrnType(Long oid, Long id);
	
	public List<GrnType> selectGrnTypeList(Long oid, Integer status);
}
