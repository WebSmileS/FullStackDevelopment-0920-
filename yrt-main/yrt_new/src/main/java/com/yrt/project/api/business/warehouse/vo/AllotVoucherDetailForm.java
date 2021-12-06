package com.yrt.project.api.business.warehouse.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import com.yrt.common.dict.AllotDetailStatus;
import com.yrt.common.exception.ValidateException;
import com.yrt.common.utils.MessageUtils;
import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
//@NoArgsConstructor
@AllArgsConstructor
public class AllotVoucherDetailForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** 经销商内部编号 */
	@ApiModelProperty(value="经销商/厂商id",required=true)
	private Long vd_inner_sn;
	@ApiModelProperty(value="经销商/厂商名称",required=true)
	private String vd_name;
	/** 产品内部编号 */
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message = "产品ID不能为空")
	private Long product_inner_sn;
	@ApiModelProperty(value="产品名称",required=true)
	@NotNull(message = "产品名称不能为空")
	private String product_name;
	/** 型号规格内部编号 */
	@ApiModelProperty(value="型号规格",required=true)
	@NotNull(message = "型号规格不能为空")
	private Long specification_inner_sn;
	@ApiModelProperty(value="型号规格名称",required=true)
	@NotNull(message = "型号规格名称不能为空")
	private String specification;
	/** 单位内部编号 */
	@ApiModelProperty(value="单位ID",required=true)
	@NotNull(message = "单位ID不能为空")
	private Long unit_inner_sn;
	@ApiModelProperty(value="单位名称",required=true)
	@NotNull(message = "单位名称不能为空")
	private String unit_name;
	/** 排序号 */
	@ApiModelProperty(value="排序号",required=true)
	@NotNull(message = "排序号不能为空")
	private Integer sort_number;
	@ApiModelProperty(value="批号",required=false)
	private String batch_number = "";
	/** 计划数量 */
	@ApiModelProperty(value="调拨数量",required=true)
	@NotNull(message = "调拨数量不能为空")
	private BigDecimal allot_quantity;
	/** 达成数量 */
//	@ApiModelProperty(value="达成数量",required=false)
//	private BigDecimal achieve_quantity = new BigDecimal(0);
	/** 状态 */
	@ApiModelProperty(value="状态",required=true)
	@NotNull(message = "状态不能为空")
	private AllotDetailStatus status;

	@ApiModelProperty(value="单价",required=false)
//	@NotNull(message = "单价不能为空")
	private BigDecimal unit_price = new BigDecimal(0);
	
	/** 产品类型 0:经销商(自建)产品   1:厂商产品 */
	@ApiModelProperty(value="产品类型 0:经销商(自建)产品   1:厂商产品",required=true)
	@NotNull(message = "产品类型不能为空")
	private Integer product_type;

	@ApiModelProperty(value="不需要前端输入",hidden=true)
	public void validated() {
		if(product_type == 0 && vd_inner_sn == null && StringUtils.isNotEmpty(vd_name)) {
			throw new ValidateException(MessageUtils.message("param.not.null","经销商ID或者经销商名称"));
		}
		if(product_type == 1 && vd_inner_sn == null && StringUtils.isNotEmpty(vd_name)) {
			throw new ValidateException(MessageUtils.message("param.not.null","厂商ID或者厂商名称"));
		}
	}

}
