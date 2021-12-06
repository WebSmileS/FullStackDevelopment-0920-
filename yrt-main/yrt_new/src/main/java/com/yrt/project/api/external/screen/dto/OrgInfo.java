package com.yrt.project.api.external.screen.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrgInfo {
    @ApiModelProperty(value = "机构ID")
    private Long org_id;
    @ApiModelProperty(value = "机构名字")
    private String org_name;
    @ApiModelProperty(value = "机构类型")
    private int type;
}
