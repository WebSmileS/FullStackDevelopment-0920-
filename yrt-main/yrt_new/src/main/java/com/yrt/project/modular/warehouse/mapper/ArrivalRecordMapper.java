package com.yrt.project.modular.warehouse.mapper;

import java.util.List;
import java.util.Map;

import com.yrt.project.modular.order.domain.PurchasingRecordProduct;
import com.yrt.project.modular.warehouse.domain.ArrivalRecord;
import com.yrt.project.modular.warehouse.domain.ArrivalRecordDetail;
import com.yrt.project.modular.warehouse.domain.CheckPurchasingRecord;

public interface ArrivalRecordMapper {

	public List<ArrivalRecord> selectArrivalRecordList(Map<String,Object> params);
	public List<ArrivalRecordDetail> selectArrivalRecordDetailList(Map<String,Object> params);
	
	public ArrivalRecord selectArrivalRecordInfo(Long id);
	
	public void insertArrivalRecord(ArrivalRecord params);
	public void updateArrivalRecord(Map<String,Object> params);
	public void deleteArrivalRecord(Map<String,Object> params);
	
	public void insertArrivalRecordDetail(ArrivalRecordDetail params);
	public void updateArrivalRecordDetail(Map<String,Object> params);
	public void deleteArrivalRecordDetail(Map<String,Object> params);
	
	public Boolean isExistArrivalRecord(Map<String,Object> params);
	public List<CheckPurchasingRecord> selectExistArrivalRecord(Map<String,Object> params);
	
	public List<PurchasingRecordProduct> selectControlUsablePurchasingRecordList(Map<String, Object> params);
}
