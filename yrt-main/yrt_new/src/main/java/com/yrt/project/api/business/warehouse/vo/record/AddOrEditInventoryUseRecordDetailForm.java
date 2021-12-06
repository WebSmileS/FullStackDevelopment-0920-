package com.yrt.project.api.business.warehouse.vo.record;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.yrt.common.dict.AllotDetailStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AddOrEditInventoryUseRecordDetailForm {

    @ApiModelProperty(value = "机构内部编号",required = true)
    private Long hospital_inner_sn;

    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "明细内部编号",required = true)
    private Long detail_inner_sn;

    @ApiModelProperty(value = "使用记录单内部编号")
    private Long rv_inner_sn;

    @ApiModelProperty(value = "产品厂商(product_type=0时为经销商，product_type=1时为厂商)内部编号",required = true)
    private Long vd_inner_sn;
    @ApiModelProperty(value = "产品厂商(product_type=0时为经销商，product_type=1时为厂商)名称",required = true)
    private String vd_name;
    @ApiModelProperty(value = "产品类型 0:经销商(自建)产品   1:厂商产品",required = true)
    private Integer product_type;

    @ApiModelProperty(value = "产品内部编号",required = true)
    private Long product_inner_sn;
    @ApiModelProperty(value = "产品名称",required = true)
    private String product_name;
    @ApiModelProperty(value = "型号规格内部编号")
    private Long specification_inner_sn;
    @ApiModelProperty(value = "型号规格")
    private String specification;

    @ApiModelProperty(value="单价")
    private BigDecimal unit_price = new BigDecimal(0);

    @ApiModelProperty(value = "使用数量",required = true)
    private BigDecimal quantity;

    @ApiModelProperty(value = "状态，借用调拨明细状态，只有0=草稿、1=确认、4=已删除3个状态")
    private AllotDetailStatus status;

    @ApiModelProperty(value = "批号")
    private String batch_number;

    @ApiModelProperty(value = "单位内部编号")
    private Long unit_inner_sn;
    @ApiModelProperty(value = "单位名称")
    private String unit_name;

    @ApiModelProperty(value = "医保编号")
    private String default_health_care_sn;

    public void setStatus(int status) {
        switch (status){
            case 1: this.status=AllotDetailStatus.CONFIRM;break;
            case 4: this.status=AllotDetailStatus.DISCARD;break;
            default:
                this.status=AllotDetailStatus.DRAFT;
        }
    }
}
