package com.yrt.project.api.business.warehouse.vo.grn;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchGrnForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "经手人", required = false)
    private String handler_name;

    @ApiModelProperty(value = "当前机构ID", required = true)
    @NotNull(message = "当前机构ID不能为空")
    private Long org_id;

    @ApiModelProperty(value = "仓库ID", required = false)
    private Long warehouse_model_inner_sn;

    @ApiModelProperty(value = "单据编号", required = false)
    private String grn_sn;

    @ApiModelProperty(value = "开始日期", required = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date begin_date;
    @ApiModelProperty(value = "截止日期", required = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date end_date;

    @ApiModelProperty(value = "搜索关键字", required = false)
    private String key;

    @ApiModelProperty(value = "搜索产品名字", required = false)
    private String product_name;

    @ApiModelProperty(value = "状态：-1-全部 0-正式 1-已审核 2-已入库 3-已入库复核", required = false)
    private Integer status = -1;

    @ApiModelProperty(hidden = true)
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("org_id", org_id);
        info.put("handler_name", handler_name);
        info.put("warehouse_model_inner_sn", warehouse_model_inner_sn);
        info.put("grn_sn", grn_sn);
        info.put("begin_date", begin_date);
        info.put("end_date", end_date);
        info.put("key", key);
        info.put("product_name", product_name);

        if (status.equals(-1)) {
            info.put("status", null);
        } else {
            info.put("status", status);
        }
        return info;
    }
}
