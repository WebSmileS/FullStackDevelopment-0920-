package com.yrt.project.api.external.screen.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrgWarnInfo {
    @ApiModelProperty(value = "机构名称")
    private String orgName;

    @ApiModelProperty(value = "统一社会信用代码")
    private String social_credit_code;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "预警类型")
    private String warnType;

    @ApiModelProperty(value = "机构类型")
    private Integer type;

    @ApiModelProperty(value = "联系电话")
    private String phone;

    @ApiModelProperty(value = "业务往来机构数量")
    private long friendOrgNum;

    @ApiModelProperty(value = "产品往来种类数量")
    private long friendProductCategoryNum;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "最近交易时刻")
    private LocalDateTime lastTradeAt;
}
