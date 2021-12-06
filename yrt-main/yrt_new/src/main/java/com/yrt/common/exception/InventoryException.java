package com.yrt.common.exception;

import java.math.BigDecimal;

public class InventoryException extends Throwable{ 

	private static final long serialVersionUID = 1L;

	protected final String message;
	
	protected final BigDecimal requestQuantity;//请求锁定量
	protected final BigDecimal requestMinUnitQuantity;//请求最小单位锁定量
	protected final BigDecimal curQuantity;//库存余量(库存可用量)
	
	public InventoryException(BigDecimal requestQuantity, BigDecimal requestMinUnitQuantity, BigDecimal curQuantity) {
		this.requestQuantity = requestQuantity;
		this.requestMinUnitQuantity = requestMinUnitQuantity;
		this.curQuantity = curQuantity;
		this.message = null;
	}
	
	public InventoryException(BigDecimal requestQuantity, BigDecimal requestMinUnitQuantity, BigDecimal curQuantity, String message) {
		this.requestQuantity = requestQuantity;
		this.requestMinUnitQuantity = requestMinUnitQuantity;
		this.curQuantity = curQuantity;
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

	public BigDecimal getCurQuantity() {
		return curQuantity;
	}
}
