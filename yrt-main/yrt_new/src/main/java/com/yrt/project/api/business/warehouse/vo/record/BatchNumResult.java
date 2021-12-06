package com.yrt.project.api.business.warehouse.vo.record;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BatchNumResult {
    @ApiModelProperty(value="批号，空批号表示无批号")
    private String batch_number;
    @ApiModelProperty(value="生产日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date produce_date;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value="失效日期")
    private Date overdue_date;
    @ApiModelProperty(value="平均单价")
    private Long average_price;
    @ApiModelProperty(value="库存数量")
    private BigDecimal quantity;
}
