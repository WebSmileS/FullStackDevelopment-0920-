package com.yrt.common.constant;

public enum OdoRefType {

	ODO_REF_NONE(0, "无关联"),
	ODO_REF_GRANT(1, "发放单"),
	ODO_REF_SALERECORD(2, "销售单");
	
	private Integer code;
    private String value;
    
    OdoRefType(Integer code, String value){
    	this.code = code;
    	this.value = value;
    }

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
    
    
}
