package com.yrt.project.modular.warning.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.warning.domain.ProductValidityWarning;

@Repository
public interface ProductValidityWarningMapper {
	
	public void insertProductValidityWarning(ProductValidityWarning params);
	
	public void trueDeleteProductValidityWarning(Map<String,Object> params);
	
	public List<ProductValidityWarning> selectProductValidityWarningList(Map<String,Object> params);
	
	/**
	 * 从库存产品中获取满足效期预警的产品列表
	 * @param params
	 * @return
	 */
	public List<ProductValidityWarning> checkHospitalExpireInventoryProductValidity(Map<String,Object> params);
	public List<ProductValidityWarning> checkDealerExpireInventoryProductValidity(Map<String,Object> params);
	public List<ProductValidityWarning> checkVendorExpireInventoryProductValidity(Map<String,Object> params);
}
