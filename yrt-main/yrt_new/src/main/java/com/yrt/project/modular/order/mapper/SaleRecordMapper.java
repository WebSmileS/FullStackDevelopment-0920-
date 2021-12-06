package com.yrt.project.modular.order.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.order.domain.*;

@Repository
public interface SaleRecordMapper {

	//新增销售单
	public void insertSaleOrder(Map<String,Object> params);
	public void insertDealerSaleRecordDetail(Map<String,Object> params);
	public void insertVendorSaleRecordDetail(Map<String,Object> params);
	//编辑销售单
	public int updateSaleRecord(Map<String,Object> params);
	public int updateDealerSaleRecordDetail(Map<String,Object> params);
	public int updateVendorSaleRecordDetail(Map<String,Object> params);
	//删除销售单
	public int deleteSaleRecord(Map<String,Object> params);
	public int deleteDealerSaleRecordDetail(Map<String,Object> params);
	public int deleteVendorSaleRecordDetail(Map<String,Object> params);
	
	//销售单查询
	public List<SaleRecord> selectSaleRecordList(Map<String,Object> params);
	public List<Sl> searchSaleRecord(Map<String,Object> params);
//	public List<Sl> searchSaleRecord4Phone(Map<String,Object> params);
	public Sl selectSaleRecordInfo(Map<String,Object> params);
	//销售单明细查询
	public List<SaleRecordDetail> selectSaleRecordDetailList(Map<String,Object> params);
	public List<SlDetail> searchSaleRecordDetailList(Map<String,Object> params);
	
	//检查销售单明细是否已全部生成出库单
	public List<Map<String,Object>> checkSaleRecordFinish(Map<String, Object> params);
}
