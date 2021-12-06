package com.yrt.project.modular.warning.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.warning.domain.ProductMdrfWarning;

@Repository
public interface ProductMdrfWarningMapper {
	
	public void insertProductMdrfWarning(ProductMdrfWarning params);
	
	public void trueDeleteProductMdrfWarning(Map<String,Object> params);
	
	public List<ProductMdrfWarning> selectProductMdrfWarningList(Map<String,Object> params);
	
	/**
	 * 从产品库中获取满足预警条件的产品注册证信息列表
	 * @param params
	 * @return
	 */
	public List<ProductMdrfWarning> checkExpireProductMdrf(Map<String,Object> params);
}
