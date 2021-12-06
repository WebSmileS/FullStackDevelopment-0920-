package com.yrt.project.modular.warehouse.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.order.domain.PurchasingRecordProduct;
import com.yrt.project.modular.warehouse.domain.CheckPurchasingRecord;
import com.yrt.project.modular.warehouse.domain.InspectionRecord;
import com.yrt.project.modular.warehouse.domain.InspectionRecordDetail;

@Repository
public interface InspectionRecordMapper {

	public List<InspectionRecord> selectInspectionRecordList(Map<String,Object> params);
	public List<InspectionRecordDetail> selectInspectionRecordDetailList(Map<String,Object> params);
	
	public InspectionRecord selectInspectionRecordInfo(Long id);
	
	public void insertInspectionRecord(InspectionRecord params);
	public void updateInspectionRecord(Map<String,Object> params);
	public void deleteInspectionRecord(Map<String,Object> params);
	
	public void insertInspectionRecordDetail(InspectionRecordDetail params);
	public void updateInspectionRecordDetail(Map<String,Object> params);
	public void deleteInspectionRecordDetail(Map<String,Object> params);
	
	public CheckPurchasingRecord selectExistInspectionRecord(Map<String,Object> params);
	public Boolean isExistInspectionRecord(Map<String,Object> params);
	
	public List<PurchasingRecordProduct> selectControlUsablePurchasingRecordList(Map<String, Object> params);
	
	public BigDecimal sumInspectionRecordDetailByArrival(Long arrival_detail_id);
}
