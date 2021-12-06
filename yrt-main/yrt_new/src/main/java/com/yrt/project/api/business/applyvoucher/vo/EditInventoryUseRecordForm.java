package com.yrt.project.api.business.applyvoucher.vo;

import com.yrt.project.api.business.warehouse.vo.record.AddOrEditInventoryUseRecordDetailForm;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class EditInventoryUseRecordForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="机构ID",required=true)
	@NotNull(message = "机构ID不能为空")
	private Long org_id;

	@ApiModelProperty(value="库存使用记录单ID",required=true)
	@NotNull(message = "库存使用记录单id不能为空")
	private Long rv_inner_sn;

	@ApiModelProperty(value = "仓库内部编号")
	private Long warehouse_inner_sn;

	@ApiModelProperty(value="使用人名字")
	private String use_man;

	@ApiModelProperty(value="使用类型")
	private Integer use_type;

	@ApiModelProperty(value = "病历号")
	private String medical_no;

	@ApiModelProperty(value = "医保号")
	private String health_no;

	@ApiModelProperty(value="产品列表",required=true)
	@NotNull(message = "产品列表不能为空")
	private List<AddOrEditInventoryUseRecordDetailForm> details;
}
