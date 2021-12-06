package com.yrt.project.api.business.warehouse.vo.allot;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yrt.common.dict.AllotDetailStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOrEditProductDetailForm {

    @ApiModelProperty(value = "明细id",notes = "更新操作时，需传入该参数")
    private Long detail_inner_sn;

    @ApiModelProperty(value = "产品厂商(product_type=0时为经销商，product_type=1时为厂商)内部编号")
    private Long vd_inner_sn;
    @ApiModelProperty(value = "产品厂商(product_type=0时为经销商，product_type=1时为厂商)名称")
    private String vd_name;
    @ApiModelProperty(value = "产品类型 0:经销商(自建)产品   1:厂商产品")
    private Integer product_type;

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

    @ApiModelProperty(value = "状态 0-草稿 1-确认 2-已出库 3-完结 4-作废")
    private AllotDetailStatus status;
}
