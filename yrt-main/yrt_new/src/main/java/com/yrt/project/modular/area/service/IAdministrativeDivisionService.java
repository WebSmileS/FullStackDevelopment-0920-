package com.yrt.project.modular.area.service;

import java.util.List;

import com.yrt.project.modular.area.domain.AdministrativeDivision;

public interface IAdministrativeDivisionService {
	
	/**
	 * 查询全部省份列表
	 * @return
	 */
	public List<AdministrativeDivision> selectProvinceList();
	
	/**
	 * 根据上级id查询下级列表
	 * @param id
	 * @return
	 */
	public List<AdministrativeDivision> selectChildrenList(Long id);
	
	/**
	 * 根据下级id查询上级
	 * @return
	 */
	public void selectParent(AdministrativeDivision admin, Long id);
	
	/**
	 * 根据ID 查询本身信息
	 * @param id
	 * @return
	 */
	public AdministrativeDivision selectDivisionById(Long id);
	
}
