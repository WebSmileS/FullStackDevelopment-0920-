package com.yrt.project.modular.product.domain;

import com.yrt.framework.aspectj.lang.annotation.Excel;
import com.yrt.framework.aspectj.lang.annotation.Excel.ColumnType;
import com.yrt.framework.aspectj.lang.annotation.Excel.Type;

import lombok.Data;

@Data
public class ImportProduct {

	@Excel(name = "序号", cellType = ColumnType.NUMERIC, prompt = "序号")
	private Long id;
	/** 产品名称 */
	@Excel(name = "产品名称", cellType = ColumnType.STRING, prompt = "产品名称")
	private String goods_name;
	/** 产品规格型号 */
	@Excel(name = "产品规格型号", cellType = ColumnType.STRING, prompt = "产品规格型号")
	private String goods_spec;
	/** 产品编码 */
	@Excel(name = "产品编码", cellType = ColumnType.STRING, prompt = "产品编码")
	private String product_vendor_sn;
	/** 产品包装单位 */
	@Excel(name = "产品包装单位", cellType = ColumnType.STRING, prompt = "产品包装单位")
	private String product_unit;
	/** 厂商名称 */
	@Excel(name = "厂商名称", cellType = ColumnType.STRING, prompt = "厂商名称")
	private String mfg_name;
	/** 产品注册证 */
	@Excel(name = "产品注册证", cellType = ColumnType.STRING, prompt = "产品注册证")
	private String goods_cert_no;
	/** 产品注册证起始日期 */
	@Excel(name = "注册证起始日期", cellType = ColumnType.STRING, prompt = "注册证起始日期")
	private String start_date;
	/** 产品注册证截止日期 */
	@Excel(name = "注册证截止日期", cellType = ColumnType.STRING, prompt = "注册证截止日期")
	private String end_date;
	/** 生产地址*/
	@Excel(name = "生产地址", cellType = ColumnType.STRING, prompt = "生产地址")
	private String production_address="";
	/** 结构组成*/
	@Excel(name = "结构组成", cellType = ColumnType.STRING, prompt = "结构组成")
	private String structure_composition="";
	/** 适用范围*/
	@Excel(name = "适用范围", cellType = ColumnType.STRING, prompt = "适用范围")
	private String scope_application="";
	/** 产品标准*/
	@Excel(name = "产品标准", cellType = ColumnType.STRING, prompt = "产品标准")
	private String product_tandards="";
	/** 产商地址*/
	@Excel(name = "产商地址", cellType = ColumnType.STRING, prompt = "产商地址")
	private String vendor_address="";
	/** 自定义分类 */
	@Excel(name = "自定义分类", cellType = ColumnType.STRING, prompt = "自定义分类")
	private String type = "通用";
	/** 描述**/
	@Excel(name = "产品描述", cellType = ColumnType.STRING, prompt = "描述")
	private String description="";
	@Excel(name = "错误信息", cellType = ColumnType.STRING, prompt = "错误信息", type = Type.EXPORT)
	private String msg;
}
