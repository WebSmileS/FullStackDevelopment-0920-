package com.yrt.project.modular.warehouse.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.RefVoucherType;

import lombok.Data;

@Data
public class VoucherDetailQuantity implements Serializable, Comparable<VoucherDetailQuantity> {

	private static final long serialVersionUID = 1L;

	private RefVoucherType voucher_type_c;
	private Long warehouse_inner_sn;
	private Long voucher_inner_sn;
	
	private Long voucher_details_inner_sn;
	
	private String product_name;
	private String specification;
//	private String name;
	
	private BigDecimal measure = BigDecimal.ZERO;
	private BigDecimal totalQuantity = BigDecimal.ZERO;
	private BigDecimal srcQuantity = BigDecimal.ZERO;
	private BigDecimal fixQuantity = BigDecimal.ZERO;
	
	private BillsType billsType;

	@Override
	public int compareTo(VoucherDetailQuantity o) {
		return fixQuantity.compareTo(o.getFixQuantity());
	}
	
}
