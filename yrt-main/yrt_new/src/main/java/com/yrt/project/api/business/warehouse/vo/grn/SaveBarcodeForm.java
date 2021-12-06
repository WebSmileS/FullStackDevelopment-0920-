package com.yrt.project.api.business.warehouse.vo.grn;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.BillsType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SaveBarcodeForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	@ApiModelProperty(value="相关机构ID",required=true)
	@NotNull(message = "相关机构ID不能为空")
	private Long organization_inner_sn;
	@ApiModelProperty(value="相关机构名称",required=true)
	@NotBlank(message = "相关机构名称不能为空")
	private String organization_name;
	@ApiModelProperty(value="仓库ID",required=false)
	private Long warehouse_inner_sn;
	@ApiModelProperty(value="仓库名称",required=false)
	private String warehouse_name;
	@ApiModelProperty(value="产品厂商ID",required=true)
	@NotNull(message = "产品厂商ID不能为空")
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value="产品厂商名称",required=true)
	@NotNull(message = "产品厂商名称不能为空")
	private String product_vendor_name;
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message = "产品ID不能为空")
	private Long product_inner_sn;
	@ApiModelProperty(value="产品名称",required=true)
	@NotNull(message = "产品名称不能为空")
	private String product_name;
	@ApiModelProperty(value="型号规格ID",required=true)
	@NotNull(message = "型号规格ID不能为空")
	private Long specification_inner_sn;
	@ApiModelProperty(value="型号规格",required=true)
	@NotNull(message = "型号规格不能为空")
	private String specification;
	@ApiModelProperty(value="包装单位ID",required=true)
	@NotNull(message = "包装单位ID不能为空")
	private Long unit_inner_sn;
	@ApiModelProperty(value="单位",required=true)
	@NotNull(message = "单位不能为空")
	private String unit_name;
	@ApiModelProperty(value="登记表内部编号ID",required=false)
	private Long mdrf_inner_sn;
	@ApiModelProperty(value="医疗器械注册证号",required=false)
	private String mdrf_sn;
	@ApiModelProperty(value="产品批号",required=true)
	@NotNull(message = "产品批号不能为空")
	private String batch_number;
	@ApiModelProperty(value="条码类别 0-单一条码 1-多条码",required=true)
	@NotNull(message = "条码类别不能为空")
	private Integer barcode_type;
	@ApiModelProperty(value="生产日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date produce_date;
	@ApiModelProperty(value="失效日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date overdue_date;
	@ApiModelProperty(value="产品数",required=true)
	@NotNull(message = "产品数不能为空")
	private BigDecimal product_number;
	@ApiModelProperty(value="入库单ID",required=true)
	@NotNull(message = "入库单ID不能为空")
	private Long grn_inner_sn;
	@ApiModelProperty(value="入库单明细ID",required=true)
	@NotNull(message = "入库单明细ID不能为空")
	private Long grn_detail_inner_sn;
	@ApiModelProperty(value="验货单ID",required=false)
	private Long pp_inner_sn;
	@ApiModelProperty(value="验货单明细ID",required=false)
	private Long pp_detail_inner_sn;
	@ApiModelProperty(value="唯一编码",required=true)
	@NotNull(message = "唯一编码不能为空")
	private List<Map<String,String>> codes;

	@ApiModelProperty(value="产品类型 0:经销商(自建)产品   1:厂商产品",required=true)
	@NotNull(message = "产品类型不能为空")
	private Integer product_type;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
}
