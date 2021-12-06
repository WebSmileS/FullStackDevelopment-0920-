package com.yrt.project.api.vo;

import com.yrt.common.dict.RefVoucherType;

import lombok.Data;

@Data
public class VoucherKey {
	public String batchNumber;
	public RefVoucherType voucherType;
	public Long voucherId;
	public Long voucherDetailId;
}