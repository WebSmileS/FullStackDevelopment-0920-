package com.yrt.project.modular.area.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yrt.project.modular.area.domain.AdministrativeDivision;
import com.yrt.project.modular.area.mapper.AdministrativeDivisionMapper;

@Service
public class AdministrativeDivisionServiceImpl implements IAdministrativeDivisionService {
	
	@Autowired
	private AdministrativeDivisionMapper mapper;

	@Override
	public List<AdministrativeDivision> selectProvinceList() {
		return mapper.selectProvinceList();
	}

	@Override
	public List<AdministrativeDivision> selectChildrenList(Long id){
		return mapper.selectChildrenList(id);
	}

	@Override
	public void selectParent(AdministrativeDivision admin, Long pid){
		if(pid != null) {
			AdministrativeDivision parent = selectDivisionById(pid);
			admin.setParent(parent);
			selectParent(parent, parent.getParent_administrative_division_sn());
		}
	}

	@Override
	public AdministrativeDivision selectDivisionById(Long id) {
		return mapper.selectDivisionById(id);
	}
	
}
