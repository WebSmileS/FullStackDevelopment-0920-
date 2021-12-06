package com.yrt.project.modular.finance.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.yrt.project.modular.finance.domain.*;

public interface OdoRecordMapper {

	public List<OdoRecord> selectDealerOdoRecordList(Map<String,Object> params);
	public BigDecimal sumDealerOdoRecordList(Map<String,Object> params);
	public List<OdoRecord> selectHospitalOdoRecordList(Map<String,Object> params);
	public BigDecimal sumtHospitalOdoRecordList(Map<String,Object> params);
	public List<OdoRecord> selectVendorOdoRecordList(Map<String,Object> params);
	public BigDecimal sumVendorOdoRecordList(Map<String,Object> params);
	
}
