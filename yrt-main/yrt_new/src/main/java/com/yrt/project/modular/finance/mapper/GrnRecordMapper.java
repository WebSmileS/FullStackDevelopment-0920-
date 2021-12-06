package com.yrt.project.modular.finance.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.yrt.project.modular.finance.domain.*;

public interface GrnRecordMapper {

	public List<GrnRecord> selectDealerGrnRecordList(Map<String,Object> params);
	public BigDecimal sumDealerGrnRecordList(Map<String,Object> params);
	public List<GrnRecord> selectHospitalGrnRecordList(Map<String,Object> params);
	public BigDecimal sumHospitalGrnRecordList(Map<String,Object> params);
	public List<GrnRecord> selectVendorGrnRecordList(Map<String,Object> params);
	public BigDecimal sumVendorGrnRecordList(Map<String,Object> params);
	
}
