package com.yrt.project.api.common.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yrt.common.dict.BaseIntEnum;
import com.yrt.common.dict.SystemType;
import com.yrt.common.dict.VoucherType;

import lombok.Data;

/**
 * 单据编号 实用服务 replace {@link com.yrt.common.utils.OrderNumberUtils}
 */
@Service
public class VoucherNumberService {

	private static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	private static final String voucherSnTable = "voucher_sn"; // max_value: SMALLINT(short)=32767
	private static final String SN_FORMAT = "%s%s%04d"; // prefix + yyyyMMdd + NNNN (LEN=14+,
														// dealer_hospital_sl.sl_sn=48)

	@Autowired
	private MaxSnService snService;

	/**
	 * 生成单据号
	 * @param voucherType
	 * @param systemType
	 * @param orgId
	 * @return
	 */
	public String getMaxSn(VoucherType voucherType, SystemType systemType, long orgId) {
		Date date = new Date();
		Map<String, Object> keys = new HashMap<>();
		keys.put("system_type", systemType);
		keys.put("belong", orgId); // org
		keys.put("voucher_type", voucherType);
		keys.put("sn_date", date); // db type: date(no time part)

		long sn = snService.getMaxSn(voucherSnTable, keys);
		return String.format(SN_FORMAT, voucherType.prefix(), yyyyMMdd.format(date), sn);
	}

	//////////////////////////////////////////////////////////////////////////////////////

	/**
	 * same as {@link #getMaxSn(VoucherType, SystemType, long)}.<br>
	 *  int形式的systemType
	 * 
	 */
	public String getMaxSn(VoucherType voucherType, int systemType, long orgId) {
		return getMaxSn(voucherType, BaseIntEnum.valueOf(SystemType.class, systemType), orgId);
	}

	
	/**
	 * Util method for get order number pair(purchase--po/CG, sale--so/XS) for two org:<br>
	 * 1. <strong>po</strong> for systemType1/partA(purchase: 采购订单号/CG) <br>
	 * 2. <strong>so</strong> for systemType2/partB(sale: 销售订单号/CG) <br>
	 * <p>
	 * <strong>Notes：</strong> 返回的 po/so 是 Supplier (调get()才生成单号)
	 */
	public OrderNumberPair getOrderNumberPair(SystemType systemType1, long partA, SystemType systemType2, long partB) {
		return OrderNumberPair.of(() -> getMaxSn(VoucherType.ORDER_PO, systemType1, partA),
				() -> getMaxSn(VoucherType.ORDER_SO, systemType1, partB));
	}
	
	/**
	 * same as {@link #getOrderNumberPair(SystemType, long, SystemType, long)}.<br>
	 * int形式的systemType.
	 * 
	 * <p>
	 * <strong>Notes：</strong> 返回的 po/so 是 Supplier (调get()才生成单号)
	 */
	public OrderNumberPair getOrderNumberPair(int systemType1, long partA, int systemType2, long partB) {
		return OrderNumberPair.of(() -> getMaxSn(VoucherType.ORDER_PO, systemType1, partA),
				() -> getMaxSn(VoucherType.ORDER_SO, systemType1, partB));
	}

	@Data(staticConstructor = "of")
	public static class OrderNumberPair {
		private final Supplier<String> po;
		private final Supplier<String> so;
	}

}
