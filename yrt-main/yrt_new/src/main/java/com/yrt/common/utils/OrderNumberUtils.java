package com.yrt.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.yrt.common.constant.Constants;
import com.yrt.project.modular.platform.mapper.ProceduresMapper;


/**
 * @deprecated replaced by {@link com.yrt.project.api.common.service.VoucherNumberService}
 */
//@Service
 class OrderNumberUtilsDeprecated {

	private static int VOUCHER_TYPE_CONTRACT = 0;
	private static int VOUCHER_TYPE_PLAN = 1;
	private static int VOUCHER_TYPE_ORDER = 2;
	private static int VOUCHER_TYPE_SALE = 3;
	private static int VOUCHER_TYPE_PUTOUT = 4;
	private static int VOUCHER_TYPE_PUTIN = 5;
	private static int VOUCHER_TYPE_APPLY = 6;
	private static int VOUCHER_TYPE_GRANT = 7;
	private static int VOUCHER_TYPE_STORAGE = 8;
	private static int VOUCHER_TYPE_PICK = 9;
	private static int VOUCHER_TYPE_INQUIRY = 10;
//	private static int VOUCHER_TYPE_CONTRACT = 11;
	private static int VOUCHER_TYPE_INSPECTION = 12;
	
	@Autowired private ProceduresMapper mapper;
	
	/**
	 * 获取合同单最大编号
	 * @param systemType
	 * @param oid
	 * @return
	 */
	public String getContractMaxSn(Integer systemType, Long oid) {
		StringBuffer sb = new StringBuffer("HT");
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String sdate = s.format(date);
		sb.append(sdate);
		Map<String, Object> params = new HashMap<>();
		params.put("system_type_i", systemType);
		params.put("belong_i", oid);
		params.put("voucher_type_i", VOUCHER_TYPE_CONTRACT);
		params.put("sn_date_i", date);
		String max_sn = mapper.getMaxVoucherSn(params);
		sb.append(max_sn);
		return sb.toString();
	}
	
	/**
	 * 获取采购计划单最大编号
	 * @param systemType
	 * @param oid
	 * @return
	 */
	public String getPlanMaxSn(Integer systemType, Long oid) {
		StringBuffer sb = new StringBuffer("JH");
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String sdate = s.format(date);
		sb.append(sdate);
		Map<String, Object> params = new HashMap<>();
		params.put("system_type_i", systemType);
		params.put("belong_i", oid);
		params.put("voucher_type_i", VOUCHER_TYPE_PLAN);
		params.put("sn_date_i", date);
		String max_sn = mapper.getMaxVoucherSn(params);
		sb.append(max_sn);
		return sb.toString();
	}
	
	/**
	 * 获取采购订单和销售订单最大编号
	 * @param systemType
	 * @param oid
	 * @return String[]
	 */
	public String[] getOrderMaxSn(Integer systemType1, Long partA, Integer systemType2, Long partB) {
		StringBuffer sb1 = new StringBuffer("CG");
		StringBuffer sb2 = new StringBuffer("XS");
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String sdate = s.format(date);
		sb1.append(sdate);
		sb2.append(sdate);
		Map<String, Object> params1 = new HashMap<>();
		params1.put("system_type_i", systemType1);
		params1.put("belong_i", partA);
		params1.put("voucher_type_i", VOUCHER_TYPE_ORDER);
		params1.put("sn_date_i", date);
		String max_sn1 = mapper.getMaxVoucherSn(params1);
		sb1.append(max_sn1);
		Map<String, Object> params2 = new HashMap<>();
		params2.put("system_type_i", systemType2);
		params2.put("belong_i", partB);
		params2.put("voucher_type_i", VOUCHER_TYPE_ORDER);
		params2.put("sn_date_i", date);
		String max_sn2 = mapper.getMaxVoucherSn(params2);
		sb2.append(max_sn2);
		String[] result = {sb1.toString(),sb2.toString()};
		return result;
	}
	
	/**
	 * 获取销售单(销货单)最大编号
	 * @param systemType
	 * @param oid
	 * @return
	 */
	public String getSaleMaxSn(Integer systemType, Long oid) {
		StringBuffer sb = new StringBuffer("XH");
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String sdate = s.format(date);
		sb.append(sdate);
		Map<String, Object> params = new HashMap<>();
		params.put("system_type_i", systemType);
		params.put("belong_i", oid);
		params.put("voucher_type_i", VOUCHER_TYPE_SALE);
		params.put("sn_date_i", date);
		String max_sn = mapper.getMaxVoucherSn(params);
		sb.append(max_sn);
		return sb.toString();
	}
	
	/**
	 * 获取出库单最大编号
	 * @param systemType
	 * @param oid
	 * @return
	 */
	public String getPutOutMaxSn(Integer systemType, Long oid) {
		StringBuffer sb = new StringBuffer("CK");
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String sdate = s.format(date);
		sb.append(sdate);
		Map<String, Object> params = new HashMap<>();
		params.put("system_type_i", systemType);
		params.put("belong_i", oid);
		params.put("voucher_type_i", VOUCHER_TYPE_PUTOUT);
		params.put("sn_date_i", date);
		String max_sn = mapper.getMaxVoucherSn(params);
		sb.append(max_sn);
		return sb.toString();
	}
	
	/**
	 * 获取入库单最大编号
	 * @param systemType
	 * @param oid
	 * @return
	 */
	public String getPutInMaxSn(Integer systemType, Long oid) {
		StringBuffer sb = new StringBuffer("RK");
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String sdate = s.format(date);
		sb.append(sdate);
		Map<String, Object> params = new HashMap<>();
		params.put("system_type_i", systemType);
		params.put("belong_i", oid);
		params.put("voucher_type_i", VOUCHER_TYPE_PUTIN);
		params.put("sn_date_i", date);
		String max_sn = mapper.getMaxVoucherSn(params);
		sb.append(max_sn);
		return sb.toString();
	}
	
	/**
	 * 获取申领单最大编号
	 * @param systemType
	 * @param oid
	 * @return
	 */
	public String getApplyMaxSn(Long oid) {
		StringBuffer sb = new StringBuffer("SL");
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String sdate = s.format(date);
		sb.append(sdate);
		Map<String, Object> params = new HashMap<>();
		params.put("system_type_i", Constants.SYSTEM_TYPE_HOSPITAL);
		params.put("belong_i", oid);
		params.put("voucher_type_i", VOUCHER_TYPE_APPLY);
		params.put("sn_date_i", date);
		String max_sn = mapper.getMaxVoucherSn(params);
		sb.append(max_sn);
		return sb.toString();
	}
	
	/**
	 * 获取发放单最大编号
	 * @param systemType
	 * @param oid
	 * @return
	 */
	public String getGrantMaxSn(Long oid) {
		StringBuffer sb = new StringBuffer("FF");
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String sdate = s.format(date);
		sb.append(sdate);
		Map<String, Object> params = new HashMap<>();
		params.put("system_type_i", 1);
		params.put("belong_i", oid);
		params.put("voucher_type_i", VOUCHER_TYPE_GRANT);
		params.put("sn_date_i", date);
		String max_sn = mapper.getMaxVoucherSn(params);
		sb.append(max_sn);
		return sb.toString();
	}
	
	/**
	 * 获取储位分配单最大编号
	 * @param systemType
	 * @param oid
	 * @return
	 */
	public String getStorageMaxSn(Integer systemType, Long oid) {
		StringBuffer sb = new StringBuffer("CW");
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String sdate = s.format(date);
		sb.append(sdate);
		Map<String, Object> params = new HashMap<>();
		params.put("system_type_i", systemType);
		params.put("belong_i", oid);
		params.put("voucher_type_i", VOUCHER_TYPE_STORAGE);
		params.put("sn_date_i", date);
		String max_sn = mapper.getMaxVoucherSn(params);
		sb.append(max_sn);
		return sb.toString();
	}
	
	/**
	 * 获取拣货单最大编号
	 * @param systemType
	 * @param oid
	 * @return
	 */
	public String getPickMaxSn(Integer systemType, Long oid) {
		StringBuffer sb = new StringBuffer("JH");
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String sdate = s.format(date);
		sb.append(sdate);
		Map<String, Object> params = new HashMap<>();
		params.put("system_type_i", systemType);
		params.put("belong_i", oid);
		params.put("voucher_type_i", VOUCHER_TYPE_PICK);
		params.put("sn_date_i", date);
		String max_sn = mapper.getMaxVoucherSn(params);
		sb.append(max_sn);
		return sb.toString();
	}
	
	/**
	 * 获取询价单最大编号
	 * @param systemType
	 * @param oid
	 * @return
	 */
	public String getInquiryMaxSn(Integer systemType, Long oid) {
		StringBuffer sb = new StringBuffer("XJ");
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String sdate = s.format(date);
		sb.append(sdate);
		Map<String, Object> params = new HashMap<>();
		params.put("system_type_i", systemType);
		params.put("belong_i", oid);
		params.put("voucher_type_i", VOUCHER_TYPE_INQUIRY);
		params.put("sn_date_i", date);
		String max_sn = mapper.getMaxVoucherSn(params);
		sb.append(max_sn);
		return sb.toString();
	}
	
	/**
	 * 获取验货单最大编号
	 * @param systemType
	 * @param oid
	 * @return
	 */
	public String getInspectionMaxSn(Integer systemType, Long oid) {
		StringBuffer sb = new StringBuffer("YH");
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String sdate = s.format(date);
		sb.append(sdate);
		Map<String, Object> params = new HashMap<>();
		params.put("system_type_i", systemType);
		params.put("belong_i", oid);
		params.put("voucher_type_i", VOUCHER_TYPE_INSPECTION);
		params.put("sn_date_i", date);
		String max_sn = mapper.getMaxVoucherSn(params);
		sb.append(max_sn);
		return sb.toString();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
