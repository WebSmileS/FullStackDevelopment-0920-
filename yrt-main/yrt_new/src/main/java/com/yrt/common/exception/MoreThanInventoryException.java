package com.yrt.common.exception;

import java.math.BigDecimal;

public class MoreThanInventoryException extends Throwable{ 

	private static final long serialVersionUID = 1L;

	protected final String message;
	
	protected final BigDecimal requestQuantity;//请求锁定量
	protected final BigDecimal requestMinUnitQuantity;//请求最小单位锁定量
	protected final BigDecimal moreThenQuantity;//超出库存总量
	
	public MoreThanInventoryException(BigDecimal requestQuantity, BigDecimal requestMinUnitQuantity, BigDecimal moreThenQuantity) {
		this.requestQuantity = requestQuantity;
		this.requestMinUnitQuantity = requestMinUnitQuantity;
		this.moreThenQuantity = moreThenQuantity;
		this.message = null;
	}
	
	public MoreThanInventoryException(BigDecimal requestQuantity, BigDecimal requestMinUnitQuantity, BigDecimal curQuantity, String message) {
		this.requestQuantity = requestQuantity;
		this.requestMinUnitQuantity = requestMinUnitQuantity;
		this.moreThenQuantity = curQuantity;
		this.message = message;
	}
	
    public String getMessage()
    {
        return message;
    }

	public BigDecimal getRequestQuantity() {
		return requestQuantity;
	}

	public BigDecimal getRequestMinUnitQuantity() {
		return requestMinUnitQuantity;
	}

	public BigDecimal getMoreThenQuantity() {
		return moreThenQuantity;
	}
}
