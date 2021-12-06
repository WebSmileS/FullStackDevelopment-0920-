package com.yrt.system.quartz.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.product.domain.Product;
import com.yrt.project.modular.product.domain.ProductMDRF;
import com.yrt.project.modular.product.domain.ProductSpecification;
import com.yrt.system.quartz.domain.RepetitiveVendor;

@Repository
public interface RepetitiveVendorMapper {
	
	/**
	 * 查询全部商品
	 * @return
	 */
	public List<RepetitiveVendor> selectAllGoods();
	
	/**
	 * 修改msg和转态
	 */
	public void updateMsgAndStatus(Map<String, Object> params);
	
	public List<Product> searchProductList(Map<String, Object> params);
	public List<ProductSpecification> selectProductSpecListById(Map<String, Object> params);
	public List<ProductMDRF> searchProductMDRFList(Map<String, Object> params);
	
	public int trueDeleteProductSpec(Map<String, Object> params);
	public int trueDeleteProductUnit(Map<String, Object> params);
	public int deleteProductImage(Map<String, Object> params);
	public int deleteMDRFImage(Map<String, Object> params);
	public int deleteProductMDRF(Map<String, Object> params);
	
	public int trueDeleteProduct(Map<String, Object> params);
	public int trueDeleteVendor(Long id);
}
