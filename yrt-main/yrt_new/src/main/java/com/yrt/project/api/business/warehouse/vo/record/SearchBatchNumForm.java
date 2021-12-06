package com.yrt.project.api.business.warehouse.vo.record;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data(staticConstructor = "of")
@NoArgsConstructor
@AllArgsConstructor
public class SearchBatchNumForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value="当前机构ID",required=true)
    @NotNull(message="当前机构ID不能为空")
    private Long org_id;

    @ApiModelProperty(value="仓库ID",required=true)
    @NotNull(message="当前仓库ID不能为空")
    private Long warehouse_inner_sn;

    @ApiModelProperty(value="产品厂商ID",required=true)
    @NotNull(message="产品厂商ID不能为空")
    private Long vd_inner_sn;

    @ApiModelProperty(value="产品ID",required=true)
    @NotNull(message="产品ID不能为空")
    private Long product_inner_sn;

    @ApiModelProperty(value="型号规格ID",required=true)
    @NotNull(message="型号规格ID不能为空")
    private Long specification_inner_sn;

    @ApiModelProperty(value="批号",required=false)
    private String batch_number;

    @ApiModelProperty(value="产品类型 0:经销商(自建)产品   1:厂商产品",required=true)
    @NotNull(message="产品类型ID不能为空")
    private  Integer product_type;
}
