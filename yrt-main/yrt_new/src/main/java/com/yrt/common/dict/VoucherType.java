package com.yrt.common.dict;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 单据类别Enum
 * <p>
 * 
 * 单据编号规则 `前缀 + 年月日 + 序号 例如：合同编号 HT201905120001`(DB存储允许最大长度48):
 * <ul>
 * <li>前缀: 2-4位 单据名称，拼音字母命名(两位容易重名)</li>
 * <li>年月日: 8位yyyyMMdd</li>
 * <li>序号: 4-5位，小于4位时前面补0(存储类型为short, 最多5位)</li>
 * </ul>
 * 
 * <strong>Changes:</strong>
 * <ol>
 * <li>采购计划 JH 变更为 CGJH (由于系统中存在不止一种计划，也避免与“拣货”冲突).</li>
 * <li>[采购订单2]/[销售订单13]： [销售订单]不再与 CG 使用相同的type2(会造成 同一机构的 [采购]/[销售]单号不连续).</li>
 * </ol>
 * 
 * <strong>Notes:</strong> 枚举常量名可自由命名(prefix 决定最终SN).
 */
public enum VoucherType implements BaseIntEnum {

	/** 合同 */
	CONTRACT(0, "HT", "合同"),

	/** 采购计划 */
	PURCHASE_PLAN(1, "CGJH", "采购计划"), // JH conflict with JH(9, "拣货单")。 由于系统中存在不止一种计划，所以重命名为CGJH

	/** 采购订单 (通常与对手机构 成对生成 【销售订单】{@link #ORDER_SO}) */
	ORDER_PO(2, "CG", "采购订单"), // 2

	// /** 销售订单 (通常与对手机构 成对生成 【采购订单】{@link #ORDER_PO}) */
	// ORDER_SO(13, "XS", "销售订单"), // 旧值2 与CG相同，改为13(切换当天会有重复单号)

	/** 销售单(销货单) */
	SALE_RECORD(3, "XH", "销售单(销货单)"),

	/** 出库单 */
	PUT_OUT(4, "CK", "出库单"),

	/** 入库单 */
	PUT_IN(5, "RK", "入库单"),

	/** 申领单 */
	APPLY(6, "SL", "申领单"),

	/** 发放单 */
	GRANT(7, "FF", "发放单"),

	/** 储位分配单 */
	STORAGE_LOC(8, "CW", "储位分配单"),

	/** 拣货单 */
	PICK(9, "JH", "拣货单"), // JH conflict with 1-采购计划(已重命名为CGJH)

	/** 询价单 */
	INQUIRY_PRICE(10, "XJ", "询价单"),

	/** 报价单 */
	OFFER_PRICE(11, "BJ", "报价单"),

	/** 验货单 */
	INSPECTION(12, "YH", "验货单"),

	/** 销售订单 (通常与对手机构 成对生成 【采购订单】{@link #ORDER_PO}) */
	ORDER_SO(13, "XS", "销售订单"), // 旧值2 与CG相同，改为13(切换当天会有重复单号)
	
	ARRIVAL_RECORD(14, "SH", "收货单"),
	
	ORTHOPAEDICS(15, "GKJH", "骨科计划"),
	
	REJECT(16, "TH", "退货单"),
	ADJUST(17, "PD", "盘点单"),
	ALLOT(18, "DB", "调拨单"),
	RECORD(19, "RE", "使用记录单"),
	LOGISTICS(20, "WLD", "物流信息单"),
	SALE_RETURN(21,"XT", "销售退货"),
	PURCHASE_RETURN(22,"JT", "采购退货"),
	SALE_GIVE(23,"ZS", "销售赠送"),
	GRN_INVOICE(24,"RKFP", "入库发票"),
	ODO_INVOICE(25,"CKFP", "出库发票"),
	ADVANCE_PAYMENT(26,"YFK", "预付款"),
	PAYMENT_ORDER(27,"FK","付款单"),
	RECEIVE_ORDER(28,"SK","收款单"),
	ADVANCE_ORDER(29,"YS","预售单"),
	;

	private static final Logger logger = LoggerFactory.getLogger(VoucherType.class);
	static {
		List<String> duplcatedPrefix = Stream.of(VoucherType.values())//
				.collect(Collectors.toMap(VoucherType::prefix, x -> 1, (a, b) -> a + b)).entrySet().stream()
				.filter((e) -> e.getValue() > 1).map(e -> e.getKey()).collect(Collectors.toList());
		if (!duplcatedPrefix.isEmpty()) {
			logger.error("重复的单据前缀定义: {}. Please check {}", duplcatedPrefix, VoucherType.class);
			// this is a code error, prevent using any VoucherType related features.
			throw new RuntimeException("重复的单据前缀定义: " + duplcatedPrefix + ". Please check " + VoucherType.class);
		}

		List<Integer> duplicatedValue = Stream.of(VoucherType.values())
				.collect(Collectors.toMap(VoucherType::value, x -> 1, (a, b) -> a + b)).entrySet().stream()
				.filter((e) -> e.getValue() > 1).map(e -> e.getKey()).collect(Collectors.toList());
		if (!duplicatedValue.isEmpty()) {
			logger.error("重复的单据类型值定义: {}. Please check {}", duplicatedValue, VoucherType.class);
			// this is a code error, prevent using any VoucherType related features.
			throw new RuntimeException("重复的单据类型值定义: " + duplicatedValue + ". Please check " + VoucherType.class);
		}
	}
	
	public static List<EnumItemDesc> descs() {
		return Stream.of(VoucherType.values()).map(e->EnumItemDesc.of(e.prefix(), e.text())).collect(Collectors.toList());
	}

	private final int value;
	private final String prefix;
	private final String text;

	private VoucherType(int value, String prefix, String text) {
		this.value = value;
		this.prefix = prefix;
		this.text = text;
	}

	public String prefix() {
		return prefix;
	}

	@Override
	public int value() {
		return value;
	}

	@Override
	public String text() {
		return text;
	}

}
