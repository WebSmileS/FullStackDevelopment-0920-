package com.yrt.project.modular.product.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.product.domain.ProductUnit;

@Repository
public interface ProductUnitMapper {
	
	public void p_add_product_unit(Map<String, Object> params);
	
	public int updateProductUnit(ProductUnit unit);
	
	public int cleanCommonUse(ProductUnit unit);
	
	public int deleteProductUnit(ProductUnit unit);
	
	public int trueDeleteProductUnit(Map<String, Object> params);
	
	public ProductUnit selectProductUnitInfo(ProductUnit unit);
	
	public List<ProductUnit> selectProductUnitList(ProductUnit unit);

	public List<ProductUnit> selectProductUnitListByPid(ProductUnit unit);
	
	public BigDecimal calculate_product_min_unit(Map<String, Object> params);
	public Long c_get_product_min_unit_inner_sn(Map<String, Object> params);
	public String c_get_product_min_unit(Map<String, Object> params);
	
	public Long existProductUnit(Map<String, Object> params);
}
