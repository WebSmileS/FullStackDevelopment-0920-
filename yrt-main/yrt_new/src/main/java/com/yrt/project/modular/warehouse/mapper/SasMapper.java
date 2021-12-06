package com.yrt.project.modular.warehouse.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.warehouse.domain.Sas;
import com.yrt.project.modular.warehouse.domain.SasDetail;
import com.yrt.project.modular.warehouse.domain.VoucherDetailQuantity;

@Repository
public interface SasMapper {

	public List<Sas> searchSasList(Map<String, Object> params);
	
	public Sas selectSasInfo(Map<String, Object> params);
	
	public List<SasDetail> selectSasDetail(Map<String, Object> params);
	
	public void insertSas(Map<String, Object> params);
	
	public int updateSas(Map<String, Object> params);
	
	public int deleteSas(Map<String, Object> params);
	
	public List<Map<String,Object>> checkSasFinish(Map<String, Object> params);
	
	public void insertSasVendorDetail(Map<String, Object> params);
	
	public int updateSasVendorDetail(Map<String, Object> params);
	
	public int deleteSasVendorDetail(Map<String, Object> params);
	
	public void insertSasDealerDetail(Map<String, Object> params);
	
	public int updateSasDealerDetail(Map<String, Object> params);
	
	public int deleteSasDealerDetail(Map<String, Object> params);
	
	public List<Map<String,Object>> selectSasId(Map<String, Object> params);
	
	public List<Map<String,Object>> selectNoHaveSasProduct(Map<String, Object> params);
	
	public List<Map<String,Object>> selectPendingProductList(Map<String, Object> params);
	
	public List<Map<String,Object>> selectPendingStorageList(Map<String, Object> params);
	
	public Map<String,Object> checkProductInfoByBarcode(Map<String, Object> params);
	
//	public List<Map<String,Object>> selectFinishLoadProductList(Map<String, Object> params);
	
	//条码、库存、仓库模型对应表
	public List<Map<String,Object>> selectBarCodeInventoryWarehouseRef(Map<String, Object> params);
	public int insertVendorBarCodeInventoryWarehouseRef(Map<String, Object> params);
	public int insertDealerBarCodeInventoryWarehouseRef(Map<String, Object> params);
	
	//储位与库存数量对应表
	public List<Map<String,Object>> selectBarCodeInventoryQuantityRef(Map<String, Object> params);
	public int insertVendorBarCodeInventoryQuantityRef(Map<String, Object> params);
	public int updateVendorBarCodeInventoryQuantityRef(Map<String, Object> params);
	public int insertDealerBarCodeInventoryQuantityRef(Map<String, Object> params);
	public int updateDealerBarCodeInventoryQuantityRef(Map<String, Object> params);
	
	//获取所有调单明细数量汇总
	public List<VoucherDetailQuantity> selectVoucherDetailQuantity(Map<String,Object> params);
}
