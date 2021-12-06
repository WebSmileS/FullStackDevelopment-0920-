package com.yrt.project.api.business.warehouse.vo;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.yrt.project.api.business.warehouse.vo.record.AddOrEditInventoryUseRecordDetailForm;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddInventoryUseRecordForm {

    @ApiModelProperty(value = "医院内部编号",required = true)
    private Long hospital_inner_sn;

    @ApiModelProperty(value = "使用记录单内部编号")
    private Long rv_inner_sn;
    @ApiModelProperty(value = "使用记录单单据编号")
    private String rv_sn;

    @ApiModelProperty(value = "仓库内部编号",required = true)
    private Long warehouse_inner_sn;
    @ApiModelProperty(value = "仓库名字",required = true)
    private String warehouse_name;

    @ApiModelProperty(value = "使用人",required = true)
    private String use_man;
    @ApiModelProperty(value = "使用类型",required = true)
    private Integer use_type;

    @ApiModelProperty(value = "经手人内部编号")
    private Long proposer_inner_sn;
    @ApiModelProperty(value="经手入名字")
    private String proposer_name;

    @ApiModelProperty(value = "病历号")
    private String medical_no;

    @ApiModelProperty(value = "医保号")
    private String health_no;

    @ApiModelProperty(value="产品列表",required=true)
    @NotNull(message = "产品列表不能为空")
    private List<AddOrEditInventoryUseRecordDetailForm> details;
}
