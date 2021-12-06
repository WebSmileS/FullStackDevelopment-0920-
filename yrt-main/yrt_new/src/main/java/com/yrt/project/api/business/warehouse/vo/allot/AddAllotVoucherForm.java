package com.yrt.project.api.business.warehouse.vo.allot;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class AddAllotVoucherForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value="机构ID",required=true)
	@NotNull(message = "机构ID不能为空")
	private Long org_id;

	@ApiModelProperty(value = "单据编号，如AL20200115122")
	private String av_sn;

	@ApiModelProperty(value="申领单单据内部编号",required=false,notes = "仅当医院端通过发放申领单存在仓库时创建调拨单会传入该字段")
	private String apply_voucher_inner_sn;

	@ApiModelProperty(value = "源仓库内部编号",required = true)
	private Long source_warehouse_inner_sn;
	@ApiModelProperty(value = "目标仓库内部编号",required = true)
	private Long target_warehouse_inner_sn;

	@ApiModelProperty(value="期望完成调拨日期",required=false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date expect_date;

	@ApiModelProperty(value="描述",required=false)
	private String description = "";

	@ApiModelProperty(value = "调拨人内部编号")
	private Long poster_inner_sn;
	@ApiModelProperty(value = "调拨人名称")
	private String poster_name;

	@ApiModelProperty(value = "确认人内部编号")
	private Long affirmant_inner_sn;
	@ApiModelProperty(value = "确认人名称")
	private String affirmant_name;

	@ApiModelProperty(value="产品列表",required=true)
	@NotNull(message = "产品列表不能为空")
	private List<AddOrEditProductDetailForm> details;
}
