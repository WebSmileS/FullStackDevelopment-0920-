package com.yrt.project.api.business.warehouse.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddWareHouseForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="父仓库ID",required=false)
	private Long parent_warehouse_model_inner_sn_i;
	
	@ApiModelProperty(value="仓库类型ID(仓库专用)",required=false)
	private Long warehouse_type_inner_sn_i;
	
	@ApiModelProperty(value="行政区划ID(仓库专用)",required=false)
	private Long administrative_division_sn_i;
	
	@ApiModelProperty(value="仓库名称",required=true)
	@NotBlank(message="仓库名称不能为空")
	@Length(max=18,message="部门名称不能超过18个字符")
	private String name_i;
	
	@ApiModelProperty(value="仓库模型编号")
	private String warehouse_model_sn_i;
	
	@ApiModelProperty(value="类别",required=true)
	@NotNull(message = "必须选择一个仓库类别")
	private Integer type_i;
	
	@ApiModelProperty(value="面积(区域专用)",required=false)
	@Digits(integer=5, fraction=2, message="面积只能是5位整数和最大2位小数的数字")
	private BigDecimal proportion_i = new BigDecimal(0);
	
	@ApiModelProperty(value="通道号(货架专用)",required=false)
	private String aisle_sn_i = "";
	
	@ApiModelProperty(value="电话(仓库专用)",required=false)
	private String phone_i = "";
	
	@ApiModelProperty(value="条码",required=false)
	private String barcode_i;
	
	@ApiModelProperty(value="地址(仓库专用)",required=false)
	private String address_i = "";
	
	@ApiModelProperty(value="描述",required=false)
	private String description_i = "";
	
	@ApiModelProperty(value="专属部门ID，多个部门ID用英文逗号‘,’分割",required=false)
	private String departments = "";
	
	@ApiModelProperty(value="员工",required=false)
	private List<WareHouseRelEmp> employees = new ArrayList<>();
	
}
