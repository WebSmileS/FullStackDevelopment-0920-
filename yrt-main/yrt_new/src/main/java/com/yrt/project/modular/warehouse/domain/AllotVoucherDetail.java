package com.yrt.project.modular.warehouse.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yrt.common.dict.AllotDetailStatus;
import com.yrt.common.dict.TransferVoucherStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
public class AllotVoucherDetail {

    @ApiModelProperty(value = "机构内部编号",required = true)
    private Long org_id;
    @ApiModelProperty(value = "机构名称")
    private String org_name;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "调拨单内部编号")
    private Long av_inner_sn;
    @ApiModelProperty(value = "调拨单单据编号")
    private String av_sn;

    @ApiModelProperty(value = "申领单内部编号",notes = "仅医院端通过申领单存在仓库时创建调拨单，该字段会有值")
    private Long apply_voucher_inner_sn;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "明细内部编号",required = true)
    private Long detail_inner_sn;

    @ApiModelProperty(value = "调拨人内部编号")
    private Long poster_inner_sn;
    @ApiModelProperty(value = "调拨人名称")
    private String poster_name;

    @ApiModelProperty(value = "确认人内部编号")
    private Long affirmant_inner_sn;
    @ApiModelProperty(value = "确认人名称")
    private String affirmant_name;

    @ApiModelProperty(value = "产品厂商(product_type=0时为经销商，product_type=1时为厂商)内部编号")
    private Long vd_inner_sn;
    @ApiModelProperty(value = "产品厂商(product_type=0时为经销商，product_type=1时为厂商)名称")
    private String vd_name;
    @ApiModelProperty(value = "产品类型 0:经销商(自建)产品   1:厂商产品")
    private Integer product_type;

    @ApiModelProperty(value = "源仓库内部编号")
    private Long source_warehouse_inner_sn;
    @ApiModelProperty(value = "源仓库名称")
    private String source_warehouse_name;
    @ApiModelProperty(value = "目标仓库内部编号")
    private Long target_warehouse_inner_sn;
    @ApiModelProperty(value = "目标仓库名称")
    private String target_warehouse_name;

    @ApiModelProperty(value = "产品内部编号")
    private Long product_inner_sn;
    @ApiModelProperty(value = "产品名称")
    private String product_name;
    @ApiModelProperty(value = "型号规格内部编号")
    private Long specification_inner_sn;
    @ApiModelProperty(value = "型号规格")
    private String specification;
    @ApiModelProperty(value = "单位内部编号")
    private Long unit_inner_sn;
    @ApiModelProperty(value = "单位名称")
    private String unit_name;
    @ApiModelProperty(value = "排序号")
    private Integer sort_number;
    @ApiModelProperty(value = "批号")
    private String batch_number;
    @ApiModelProperty(value = "调拨数量")
    private BigDecimal allot_quantity;
    @ApiModelProperty(value="单价")
    private BigDecimal unit_price;

    @ApiModelProperty(value = "生产日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date produce_date;
    @ApiModelProperty(value = "失效日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date overdue_date;

    @ApiModelProperty(value = "已出库数量")
    private BigDecimal outOfStock_quantity;
    @ApiModelProperty(value = "未出库数量")
    private BigDecimal toBeOutOfStock_quantity;

    @ApiModelProperty(value = "已入库数量")
    private BigDecimal inOfStock_quantity;
    @ApiModelProperty(value = "待入库数量")
    private BigDecimal toBeInOfStock_quantity;

    @ApiModelProperty(value = "期望调拨日期(之前)")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expect_date;

    @ApiModelProperty(value = "状态 0-草稿 1-确认 2-已出库 3-完结 4-作废")
    private AllotDetailStatus status;

    @ApiModelProperty(value = "调单业务状态 1-待完成 2-已完成")
    private TransferVoucherStatus tvStatus;

    @ApiModelProperty(value = "溢价比例")
    private BigDecimal percent;

    @ApiModelProperty(value = "售价")
    private BigDecimal salesPrice;

    public void setStatus(int status) {
        switch (status){
            case 1: this.status=AllotDetailStatus.CONFIRM;break;
            case 2: this.status=AllotDetailStatus.OUTPUT;break;
            case 3: this.status=AllotDetailStatus.COMPLETED;break;
            case 4: this.status=AllotDetailStatus.DISCARD;break;
            default:
                this.status=AllotDetailStatus.DRAFT;
        }
    }
}
