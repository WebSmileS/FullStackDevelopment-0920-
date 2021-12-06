package com.yrt.project.modular.area.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.area.domain.AdministrativeDivision;

/**
 * 行政区划  数据层
 * @author Runner
 *
 */
@Repository
public interface AdministrativeDivisionMapper{

	public List<AdministrativeDivision> selectProvinceList();
	
	public List<AdministrativeDivision> selectChildrenList(Long id);
	
	public  AdministrativeDivision selectParent(Long id);
	
	public AdministrativeDivision selectDivisionById(Long id);
}
