package com.yrt.common.dict;

/**
 * 关联单据类型: 0-无 1-发放单 2-销售单 3-采购订单 4-销售订单 5-骨科计划 6-收货单
 */
public enum RefVoucherType implements BaseIntEnum {

	NONE(0, "无关联"),
	GRANT(1, "发放单"),
	SALE_RECORD(2, "销售单"),
	ORDER_PO(3, "采购订单"),
	ORDER_SO(4, "销售订单"),
	ORTHOPAEDIC(5, "骨科计划单"),
	ARRIVAL_RECORD(6, "收货单"),
	ODO(7,"出库单"),
	ALLOT(8,"调拨单"),
	GRN(9,"入库单"),
	GRN_INVOICE(10, "入库发票"),
	ODO_INVOICE(11, "出库发票"),
	ADVANCE_PAYMENT(12,"预付款"),
	PO_RECORDS(13,"采购记录"),
    ADVANCE_SALE(14,"预售单"),
	;

	private final int value;
	private final String text;

	private RefVoucherType(int value, String text) {
		this.value = value;
		this.text = text;
	}

	@Override
	public int value() {
		return value;
	}

	@Override
	public String text() {
		return text;
	}
	
	public static RefVoucherType intToEnum(int value) {    //将数值转换成枚举值
        switch (value) {
        case 0:
            return NONE;
        case 1:
            return GRANT;
        case 2:
            return SALE_RECORD;
        case 3:
            return ORDER_PO;
        case 4:
            return ORDER_SO;
        case 5:
            return ORTHOPAEDIC;
        case 6:
            return ARRIVAL_RECORD;
        case 7:
            return ODO;
        case 8:
            return ALLOT;
        case 9:
            return GRN;
        case 10:
            return GRN_INVOICE;
        case 11:
            return ODO_INVOICE;
        case 12:
            return ADVANCE_PAYMENT;
        case 13:
            return PO_RECORDS;
        default :
            return null;
        }
    }
}
