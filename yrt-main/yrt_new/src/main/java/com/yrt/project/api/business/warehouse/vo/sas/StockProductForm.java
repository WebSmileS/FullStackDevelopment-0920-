package com.yrt.project.api.business.warehouse.vo.sas;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StockProductForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//普通放置(有储位分配单)
	public interface Stock{}
	//直接放置(无储位分配单)
	public interface DirectStock{}

	@ApiModelProperty(value="产品条码",required=false)
	private List<String> barcodes;
	
	@ApiModelProperty(value="机构ID",required=true)
	@NotNull(message = "机构ID不能为空")
	private Long org_id;
	@ApiModelProperty(value="储位分配单ID",required=false)
	@NotNull(message = "储位分配单不能为空")
	@NotNull(groups= {Stock.class}, message = "储位分配单ID不能为空")
	@Null(groups= {DirectStock.class}, message = "储位分配单ID必须为空")
	private Long sas_inner_sn;
	@ApiModelProperty(value="储位分配单明细ID",required=false)
	@NotNull(groups= {Stock.class}, message = "储位分配单明细不能为空")
	@Null(groups= {DirectStock.class}, message = "储位分配单明细必须为空")
	private Long detail_inner_sn;
	@ApiModelProperty(value="仓库ID",required=true)
	@NotNull(message = "仓库ID不能为空")
	private Long warehouse_inner_sn;
	@ApiModelProperty(value="库存ID",required=true)
	@NotNull(message = "库存ID不能为空")
	private Long inventory_inner_sn;
	@ApiModelProperty(value="储位置ID",required=true)
	@NotNull(message = "储位置ID不能为空")
	private Long location_inner_sn;
	@ApiModelProperty(value="数量",required=true)
	@NotNull(message = "数量不能为空")
	private BigDecimal quantity;
	@ApiModelProperty(value="产品类型 0:经销商(自建)产品   1:厂商产品",required=true)
	@NotNull(message = "产品类型不能为空")
	private Integer product_type;
	
	@ApiModelProperty(value="产品厂商ID",required=true)
	@NotNull(message = "产品厂商ID不能为空")
	private Long product_vendor_inner_sn;
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message = "产品ID不能为空")
	private Long product_inner_sn;
	@ApiModelProperty(value="型号规格ID",required=true)
	@NotNull(message = "型号规格ID不能为空")
	private Long specification_inner_sn;
	@ApiModelProperty(value="单位ID",required=true)
	@NotNull(message = "当前单位ID不能为空")
	private Long unit_inner_sn;
}
