package com.yrt.system.quartz.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class RepetitiveVendor implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long vendor_inner_sn;
	private String vendor_name;
//	private Long product_inner_sn;
//	private String product_name;
	/**0-删除产品 1-删除厂商和产品*/
//	private Integer deleteType;
	/** 状态 0-待处理 1-已处理 */
	private Integer status;
	private String msg;

}
