package com.yrt.project.modular.product.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.framework.aspectj.lang.annotation.Excel;

import lombok.Data;

@Data
public class UDIDeviceRecord {

	@Excel(name = "主键编号")
	private String id;
	@Excel(name = "最小销售单元产品标识")
	private String zxxsdycpbs;
	@Excel(name = "医疗器械唯一标识编码体系名称")
	private String cpbsbmtxmc;
	@Excel(name = "最小销售单元中使用单元的数量")
	private String zxxsdyzsydydsl;
	@Excel(name = "使用单元产品标识")
	private String sydycpbs;
	@Excel(name = "产品标识发布日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date cpbsfbrq;
	@Excel(name = "是否有本体直接标识", readConverterExp = "0=否,1=是")
	private Integer sfybtzjbs;
	@Excel(name = "本体产品标识与最小销售单元产品标识是否一致", readConverterExp = "0=否,1=是")
	private Integer btcpbsyzxxsdycpbssfyz;
	@Excel(name = "本体产品标识")
	private String btcpbs;
	@Excel(name = "产品名称/通用名称")
	private String cpmctymc;
	@Excel(name = "商品名称")
	private String spmc;
	@Excel(name = "规格型号")
	private String ggxh;
	@Excel(name = "是否为包类/组套类产品", readConverterExp = "0=否,1=是")
	private Integer sfwblztlcp;
	@Excel(name = "产品描述")
	private String cpms;
	@Excel(name = "产品货号或编号")
	private String cphhhbh;
	@Excel(name = "器械类别")
	private String qxlb;
	@Excel(name = "原分类编码")
	private String yflbm;
	@Excel(name = "分类编码")
	private String flbm;
	@Excel(name = "医疗器械注册人/备案人名称")
	private String ylqxzcrbarmc;
	@Excel(name = "统一社会信用代码")
	private String tyshxydm;
	@Excel(name = "医疗器械注册人/备案人英文名称")
	private String ylqxzcrbarywmc;
	@Excel(name = "注册证编号或者备案凭证编号")
	private String zczbhhzbapzbh;
	@Excel(name = "产品类别")
	private String cplb;
	@Excel(name = "磁共振（MR）安全相关信息")
	private String cgzmraqxgxx;
	@Excel(name = "是否标记为一次性使用", readConverterExp = "0=否,1=是")
	private Integer sfbjwycxsy;
	@Excel(name = "最大重复使用次数")
	private String zdcfsycs;
	@Excel(name = "是否为无菌包装", readConverterExp = "0=否,1=是")
	private Integer sfwwjbz;
	@Excel(name = "使用前是否需要进行灭菌", readConverterExp = "0=否,1=是")
	private Integer syqsfxyjxmj;
	@Excel(name = "灭菌方式")
	private String mjfs;
	@Excel(name = "其他信息的网址链接")
	private String qtxxdwzlj;
	@Excel(name = "医保编码")
	private String ybbm;
	@Excel(name = "退市日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date tsrq;
	@Excel(name = "生产标识是否包含批号", readConverterExp = "0=否,1=是")
	private Integer scbssfbhph;
	@Excel(name = "生产标识是否包含序列号", readConverterExp = "0=否,1=是")
	private Integer scbssfbhxlh;
	@Excel(name = "生产标识是否包含生产日期", readConverterExp = "0=否,1=是")
	private Integer scbssfbhscrq;
	@Excel(name = "生产标识是否包含失效日期", readConverterExp = "0=否,1=是")
	private Integer scbssfbhsxrq;
	@Excel(name = "特殊储存或操作条件")
	private String tscchcztj;
	@Excel(name = "特殊尺寸说明")
	private String tsccsm;
	@Excel(name = "公开的版本号")
	private Integer versionNumber;
	@Excel(name = "版本的发布时间")
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
	private Date versionTime;
	@Excel(name = "版本的状态", readConverterExp = "0=删除,1=新增,2=更新")
	private Integer versionStauts;
}
