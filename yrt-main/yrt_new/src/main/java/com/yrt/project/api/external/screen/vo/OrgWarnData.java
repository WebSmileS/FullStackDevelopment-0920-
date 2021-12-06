package com.yrt.project.api.external.screen.vo;

import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.external.screen.dto.OrgWarn;
import com.yrt.project.api.external.screen.dto.OrgWarnInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrgWarnData {
    @ApiModelProperty(value = "机构预警统计")
    List<OrgWarn> orgWarns;

    @ApiModelProperty(value = "机构预警分页信息")
    TableDataInfo<OrgWarnInfo> orgWarnInfoPage;
}
