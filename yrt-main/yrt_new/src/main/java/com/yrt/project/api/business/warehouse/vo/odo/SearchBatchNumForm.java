package com.yrt.project.api.business.warehouse.vo.odo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.yrt.common.dict.BillsType;
import com.yrt.common.dict.RefVoucherType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchBatchNumForm implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="当前机构ID",required=true)
	@NotNull(message="当前机构ID不能为空")
	private Long org_id;
	
	@ApiModelProperty(value="仓库ID",required=true)
	@NotNull(message="当前仓库ID不能为空")
	private Long warehouse_inner_sn;
	
	@ApiModelProperty(value="产品厂商ID",required=true)
	@NotNull(message="产品厂商ID不能为空")
	private Long product_vendor_inner_sn;
	
	@ApiModelProperty(value="产品ID",required=true)
	@NotNull(message="产品ID不能为空")
	private Long product_inner_sn;
	
	@ApiModelProperty(value="型号规格ID",required=true)
	@NotNull(message="型号规格ID不能为空")
	private Long specification_inner_sn;
	
	@ApiModelProperty(value="批号",required=false)
	private String batch_number;
	
	@ApiModelProperty(value="产品类型 0:经销商(自建)产品   1:厂商产品",required=true)
	@NotNull(message="产品类型ID不能为空")
	private Integer product_type;
	
	@ApiModelProperty(value="单据类型",required=true)
	@NotNull(message = "单据类型不能为空")
	private BillsType billsType;
	
	@ApiModelProperty(value="上游单据类型 /v1/dict/refVoucherType",required=false)
	private RefVoucherType voucher_type_c = RefVoucherType.NONE;
	@ApiModelProperty(value="上游单据ID",required=false)
	private Long voucher_inner_sn_c;
	@ApiModelProperty(value="上游单据明细ID",required=false)
	private Long voucher_detail_inner_sn_c;
	
	@ApiModelProperty(value="产品批号和数量([{'批号':'数量'}])",required=false)
	private Map<String, BigDecimal> batchNumberList = new HashMap<>();
	
	@ApiModelProperty(value="是否退货",required=false)
	private Boolean isReturn = false;
	
}
