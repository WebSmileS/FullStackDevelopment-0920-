package com.yrt.project.modular.warehouse.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.warehouse.domain.ProductBarcode;

@Repository
public interface ProductBarcodeMapper {

	public int insertProductBarcode(Map<String,Object> params);
	
	public int insertProductBarcodeRef(Map<String,Object> params);
	
	public int updateProductBarcode(Map<String,Object> params);
	
	public int cleanProductBarcode(Map<String,Object> params);
	
	public int insertDealerProductBarcode(Map<String,Object> params);
	
	public int insertDealerProductBarcodeRef(Map<String,Object> params);
	
	public int updateDealerProductBarcode(Map<String,Object> params);
	
	public int cleanDealerProductBarcode(Map<String,Object> params);
	
	public List<ProductBarcode> selectBarcode4Grn(Map<String,Object> params);
	
	public List<ProductBarcode> selectBarcode4Pp(Map<String,Object> params);
	
	public List<String> selectOnlyBarcode4Grn(Map<String,Object> params);
	
	public List<String> selectBarcodeInfo4Sas(Map<String,Object> params);
	
}
