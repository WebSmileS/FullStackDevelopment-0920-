package com.yrt.project.modular.warehouse.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yrt.common.dict.AllotDetailStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
public class AllotVoucher {
	
    @ApiModelProperty(value = "机构内部编号",required = true)
    private Long org_id;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "调拨单单据内部编号")
    private Long av_inner_sn;

    @ApiModelProperty(value = "申领单内部编号",notes = "仅医院端通过申领单存在仓库时创建调拨单，该字段会有值")
    private Long apply_voucher_inner_sn;

    @ApiModelProperty(value = "单据编号，如AL20200115122")
    private String av_sn;

    @ApiModelProperty(value = "调拨人内部编号")
    private Long poster_inner_sn;
    @ApiModelProperty(value = "调拨人名称")
    private String poster_name;

    @ApiModelProperty(value = "确认人内部编号")
    private Long affirmant_inner_sn;
    @ApiModelProperty(value = "确认人名称")
    private String affirmant_name;

    @ApiModelProperty(value = "源仓库内部编号",required = true)
    private Long source_warehouse_inner_sn;
    @ApiModelProperty(value = "目标仓库内部编号",required = true)
    private Long target_warehouse_inner_sn;

    @ApiModelProperty(value = "源仓库名字")
    private String source_warehouse_name;
    @ApiModelProperty(value = "目标仓库名字")
    private String target_warehouse_name;

    @ApiModelProperty(value = "单据生成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;
    @ApiModelProperty(value = "期望调拨日期(之前)")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expect_date;
    @ApiModelProperty(value = "摘要")
    private String description;

    @ApiModelProperty(value="产品列表",required=true)
    @NotNull(message = "产品列表不能为空")
    private List<AllotVoucherDetail> details;

    @ApiModelProperty(value="主表确认状态，明细只要有一个草稿，就是待确认状态-false，否则为已确认-true")
    private Boolean waiting_confirm;

    public Boolean getWaiting_confirm() {
        if (Objects.isNull(this.details)||this.details.isEmpty()) return null;
        return this.details.stream().anyMatch(x->AllotDetailStatus.DRAFT.equals(x.getStatus()));
    }

}
