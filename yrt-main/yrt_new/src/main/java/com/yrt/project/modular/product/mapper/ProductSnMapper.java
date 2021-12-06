package com.yrt.project.modular.product.mapper;

import java.util.List;
import java.util.Map;

import com.yrt.project.modular.product.domain.ProductSn;

public interface ProductSnMapper {

	//医保编号
	public void insertHealthCaseSn(Map<String,Object> params);
	public int updateHealthCaseSn(Map<String,Object> params);
	public int updateNotDefaultHealthCaseSn(Map<String,Object> params);
	public int trueDeleteHealthCaseSn(Map<String,Object> params);
	public String selectDefaultHealthCaseSn(Map<String,Object> params);
	public List<ProductSn> selectHealthCaseSnList(Map<String,Object> params);
	public Long existHealthCaseSn(Map<String,Object> params);
	
	//阳光流水号
	public void insertSunSn(Map<String,Object> params);
	public int updateSunSn(Map<String,Object> params);
	public int updateNotDefaultSunSn(Map<String,Object> params);
	public int trueDeleteSunSn(Map<String,Object> params);
	public String selectDefaultSunSn(Map<String,Object> params);
	public List<ProductSn> selectSunSnList(Map<String,Object> params);
	public Long existSunSn(Map<String,Object> params);
}
