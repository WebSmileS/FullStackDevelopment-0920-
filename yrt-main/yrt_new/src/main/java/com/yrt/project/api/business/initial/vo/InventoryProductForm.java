package com.yrt.project.api.business.initial.vo;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.framework.aspectj.lang.annotation.Excel;
import com.yrt.framework.aspectj.lang.annotation.Excel.Type;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InventoryProductForm {

	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="序号")
	private Long sn;
	
	@ApiModelProperty(value="ID为空表示新增，不为空表示编辑")
	private Long id;
	
	@Excel(name = "仓库ID", prompt = "仓库ID", type = Type.EXPORT)
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "机构ID不能为空")
	private Long warehouse_id;
	
	@ApiModelProperty(value="仓库名称",required=true)
	@NotBlank(message = "仓库名称不能为空")
	private String warehouse_name;
	
	@ApiModelProperty(value="厂商ID",required=true)
	@NotNull(message = "厂商ID不能为空")
	private Long vendor_id;
	
	@ApiModelProperty(value="厂商名称",required=true)
	@NotBlank(message = "厂商名称不能为空")
	private String vendor_name;
	
	@ApiModelProperty(value="产品名称ID",required=true)
	@NotNull(message = "产品名称ID不能为空")
	private Long product_id;
	
	@ApiModelProperty(value="产品名称",required=true)
	@NotBlank(message = "产品名称不能为空")
	private String product_name;
	
	@ApiModelProperty(value="产品规格型号ID",required=true)
	@NotNull(message = "产品规格型号ID不能为空")
	private Long product_spec_id;
	
	@ApiModelProperty(value="产品规格型号",required=true)
	@NotBlank(message = "产品规格型号不能为空")
	private String product_spec;
	
	@ApiModelProperty(value="产品包装单位ID",required=true)
	@NotNull(message = "产品包装单位ID不能为空")
	private Long product_unit_id;
	
	@ApiModelProperty(value="产品包装单位",required=true)
	@NotBlank(message = "产品包装单位不能为空")
	private String product_unit;
	
	@ApiModelProperty(value="批号")
	private String batch_number = "";
	
	@ApiModelProperty(value="生产日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date produce_date;
	
	@ApiModelProperty(value="失效日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date overdue_date;
	
	@ApiModelProperty(value="数量",required=true)
	@NotNull(message = "数量不能为空")
	@Positive(message = "数量必须>0")
	private BigDecimal quantity;
	
	@ApiModelProperty(value="金额",required=true)
	@NotNull(message = "金额不能为空")
	@PositiveOrZero(message = "数量必须>=0")
	private BigDecimal unit_price;
	
	@ApiModelProperty(value="产品类型",required=true)
	@NotNull(message = "产品类型不能为空")
	private Integer product_type;
	
}
