package com.yrt.project.api.business.warehouse.vo.record;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SearchInventoryUseRecordForm {

    @ApiModelProperty(value="医院ID",required=true)
    @NotNull(message = "医院不能为空")
    private Long hospital_inner_sn;

    @ApiModelProperty(value = "仓库内部编号")
    private Long warehouse_inner_sn;

    @ApiModelProperty(value="单据内部编号",required=false)
    private String rv_inner_sn;
    @ApiModelProperty(value="单据编号",required=false)
    private String rv_sn;

    @ApiModelProperty(value="单据明细内部编号",required=false)
    private String detail_inner_sn;

    @ApiModelProperty(value = "经手人内部编号")
    private Long proposer_inner_sn;
    @ApiModelProperty(value="经手人名字",required=false)
    private String proposer_name;

    @ApiModelProperty(value = "日期区间(start)")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date start_date;

    @ApiModelProperty(value = "日期区间(end)")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date end_date;
}
