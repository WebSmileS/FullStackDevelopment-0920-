package com.yrt.project.modular.product.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class TmpProductMDRFImage implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 厂商内部编号 */
	private Long vendor_inner_sn;
	/** 产品内部编号 */
	private Long product_inner_sn;
	/** 登记表内部编号 */
	private Long mdrf_inner_sn;
	/** 图片内部编号 */
	private Long image_inner_sn;
	/** 统一资源定位符 */
	private String url;
	private String file_name;
	
	private Integer source;
	private Long operation_organization;

}
