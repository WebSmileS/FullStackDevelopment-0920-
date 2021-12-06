package com.yrt.project.api.external.screen.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductWarnInfo {
    @ApiModelProperty(value = "产品ID")
    private Long product_inner_sn;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "机构名称")
    private String orgName;

    @ApiModelProperty(value = "预警类型")
    private String warnType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "过期时刻")
    private LocalDateTime expireAt;

    @ApiModelProperty(value = "规格型号")
    private String specification;

    @ApiModelProperty(value = "生产商单位ID")
    private Long vendor_inner_sn;

    @ApiModelProperty(value = "生产商单位名字")
    private String vendor_name;

    @ApiModelProperty(value = "批号")
    private String batch_number;
}
