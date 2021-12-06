package com.yrt.project.api.external.screen.vo;

import com.yrt.project.api.external.screen.dto.OrgCount;
import com.yrt.project.api.external.screen.dto.OrgType;
import com.yrt.project.api.external.screen.dto.OrgTypeCount;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrgData {
    @ApiModelProperty(value = "机构类型统计")
    private List<OrgType> otList;

    @ApiModelProperty(value = "机构区域统计")
    private List<OrgCount> ocList;

}
