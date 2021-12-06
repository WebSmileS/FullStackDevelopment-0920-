package com.yrt.project.modular.product.mapper;

import java.util.List;
import java.util.Map;

import com.yrt.project.modular.product.domain.ProductBaseRefTag;
import com.yrt.project.modular.product.domain.UDIDeviceRecord;
import com.yrt.project.modular.product.domain.UDIRefProduct;

public interface UDIRefProductMapper {

	public UDIRefProduct selectUDIRefProductInfo(Map<String,Object> params);
	
	public void insertUDIRefProduct(UDIRefProduct params);
	public void deleteUDIRefProduct(String id);
	
	public List<ProductBaseRefTag> selectProductBaseRefTagList(Map<String,Object> params);
	public ProductBaseRefTag selectProductBaseRefTagInfo(Map<String,Object> params);
	
	public UDIDeviceRecord selectUDIDeviceRecordInfo(Map<String,Object> params);
	
	public Long selectVendorId(String vendor_name);
	public Long selectProductId(Map<String,Object> params);
	public Long selectSpecificationId(Map<String,Object> params);
	
	//获取厂商产品列表(平台所有发布产品)
	public List<Map<String,Object>> selectVendorProductList(Map<String,Object> params);
}
