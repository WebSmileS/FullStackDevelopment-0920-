package com.yrt.project.modular.finance.domain;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SumOrderResult {

	private BigDecimal written_off_price;
	private Integer voucher_type_c;
	private Long voucher_inner_sn_c;
}
