package com.yrt.project.api.business.warehouse.vo.sas;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CheckBarcodeForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//普通放置(有储位分配单)
	public interface Stock{}
	//直接放置(无储位分配单)
	public interface DirectStock{}

	@ApiModelProperty(value="条码",required=true)
	@NotBlank(message = "条码不能为空")
	private String barcode;
	
	@ApiModelProperty(value="当前产品厂商ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="当前仓库ID",required=true)
	@NotNull(message = "当前仓库ID不能为空")
	private Long warehouse_inner_sn;
	
	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message = "当前机构ID不能为空")
	private Long product_vendor_inner_sn;
	/** 产品内部编号 */
	@ApiModelProperty(value="当前产品ID",required=true)
	@NotNull(message = "当前产品ID不能为空")
	private Long product_inner_sn;
	/** 型号规格内部编号 */
	@ApiModelProperty(value="当前型号规格ID",required=true)
	@NotNull(message = "当前型号规格ID不能为空")
	private Long specification_inner_sn;
	/** 单位内部编号 */
	@ApiModelProperty(value="当前单位ID",required=true)
	@NotNull(message = "当前单位ID不能为空")
	private Long unit_inner_sn;
	/** 库存内部编号 */
	@ApiModelProperty(value="当前库存ID",required=true)
	@NotNull(message = "当前库存ID不能为空")
	private Long inventory_inner_sn;
	/** 储位置内部编号 */
	@ApiModelProperty(value="当前储位置ID",required=false)
	@NotNull(groups= {Stock.class}, message = "当前储位置ID不能为空")
	@Null(groups= {DirectStock.class}, message = "当前储位置ID必须为空")
	private Long location_inner_sn;
}
