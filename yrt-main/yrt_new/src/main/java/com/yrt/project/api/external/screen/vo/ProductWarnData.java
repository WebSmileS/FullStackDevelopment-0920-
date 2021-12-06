package com.yrt.project.api.external.screen.vo;

import com.yrt.framework.web.page.TableDataInfo;
import com.yrt.project.api.external.screen.dto.ProductWarn;
import com.yrt.project.api.external.screen.dto.ProductWarnInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductWarnData {
    @ApiModelProperty(value = "产品预警统计")
    private List<ProductWarn> productWarns;

    @ApiModelProperty(value = "产品预警分页信息")
    private TableDataInfo<ProductWarnInfo> productWarnInfoPage;
}
