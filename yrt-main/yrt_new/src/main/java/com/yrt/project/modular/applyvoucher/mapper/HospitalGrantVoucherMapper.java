package com.yrt.project.modular.applyvoucher.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.yrt.project.modular.applyvoucher.domain.HospitalGrantVoucher;
import com.yrt.project.modular.applyvoucher.domain.HospitalGrantVoucherDetail;

@Repository
public interface HospitalGrantVoucherMapper {

	public List<HospitalGrantVoucher> searchGrantList(Map<String,Object> params);
	public List<Map<String,Object>> selectGrantList(Map<String,Object> params);
	public List<HospitalGrantVoucher> selectGrantVoucherList(Map<String,Object> params);
	public List<HospitalGrantVoucher> appSelectGrantVoucherList(Map<String,Object> params);
	
	default HospitalGrantVoucher selectGrantInfo(Long oid, Long gid) {
		Map<String, Object> params = new HashMap<>();
		params.put("hospital_inner_sn", oid);
		params.put("gv_inner_sn", gid);
		return selectGrantInfo(params);
	}
	public  HospitalGrantVoucher selectGrantInfo(Map<String,Object> params);
	
	public List<HospitalGrantVoucherDetail> searchGrantDetailList(Map<String,Object> params);
	
	public void insertGrant(Map<String,Object> params);
	public int updateGrant(Map<String,Object> params);
	
	public int updateGrantDetailDealer(Map<String,Object> params);
	public int updateGrantDetail(Map<String,Object> params);
	
	public List<Map<String,Object>> checkGrantFinish(Map<String, Object> params);
	
}
