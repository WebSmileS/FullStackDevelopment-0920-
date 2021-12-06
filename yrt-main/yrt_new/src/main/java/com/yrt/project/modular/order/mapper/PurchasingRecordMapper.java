package com.yrt.project.modular.order.mapper;

import java.util.List;
import java.util.Map;

import com.yrt.project.modular.order.domain.PurchasingRecord;
import com.yrt.project.modular.order.domain.PurchasingRecordProduct;

public interface PurchasingRecordMapper {

	public List<PurchasingRecord> selectPurchasingRecordList(Map<String, Object> params);
	public List<PurchasingRecord> selectPurchasingRecordListByVoucher(Map<String, Object> params);
	public PurchasingRecord selectPurchasingRecordInfo(Long id);
	
	public boolean existPurchasingRecord(Map<String, Object> params);
	
	public void insertPurchasingRecord(PurchasingRecord params);
	
	public void updatePurchasingRecord(Map<String, Object> params);
	
	public void deletePurchasingRecord(Map<String, Object> params);
	
	public List<PurchasingRecordProduct> selectControlUsablePurchasingRecordList(Map<String, Object> params);
	public List<PurchasingRecordProduct> selectNotControlUsablePurchasingRecordList(Map<String, Object> params);
}
