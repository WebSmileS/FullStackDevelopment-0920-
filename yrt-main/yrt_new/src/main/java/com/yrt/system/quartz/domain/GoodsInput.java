package com.yrt.system.quartz.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class GoodsInput implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	/** 产品名称 */
	private String goods_name;
	/** 产品规格型号 */
	private String goods_spec="";
	/** 产品编码 */
	private String product_vendor_sn;
	/** 产品包装单位 */
	private String product_unit="";
	/** 厂商名称 */
	private String mfg_name;
	/** 产品注册证 */
	private String goods_cert_no;
	/** 产品注册证起始日期 */
	private Date start_date;
	/** 产品注册证截止日期 */
	private Date end_date;
	/** 生产地址*/
	private String production_address="";
	/** 结构组成*/
	private String structure_composition="";
	/** 适用范围*/
	private String scope_application="";
	/** 产品标准*/
	private String product_tandards="";
	/** 产商地址*/
	private String vendor_address="";
	/** 自定义分类 */
	private String department = "";
	/** 描述**/
	private String description="";
	/** 产品导入状态0-未完成导入 1-完成导入 */
	private Integer status;
	/** 产品导入错误信息 */
	private String msg;
	private String mmsg;
	private String pmsg;

}
