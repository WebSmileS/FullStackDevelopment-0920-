package com.yrt.project.api.business.warehouse.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.Digits;

import org.hibernate.validator.constraints.Length;

import com.yrt.common.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditWareHouseForm implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value="父仓库ID",required=false)
	private Long parent_warehouse_model_inner_sn;
	private Long parent_warehouse_model_inner_sn_old;
	
	@ApiModelProperty(value="仓库类型ID(仓库专用)",required=false)
	private Long warehouse_type_inner_sn;
	private Long warehouse_type_inner_sn_old;
	
	@ApiModelProperty(value="行政区划ID(仓库专用)",required=false)
	private Long administrative_division_sn;
	private Long administrative_division_sn_old;
	
	@ApiModelProperty(value="仓库名称",required=false)
//	@NotBlank(message="仓库名称不能为空")
	@Length(max=18,message="部门名称不能超过18个字符")
	private String name;
	private String name_old;
	
	@ApiModelProperty(value="仓库模型编号",required=false)
	private String warehouse_model_sn;
	private String warehouse_model_sn_old;
	
	@ApiModelProperty(value="类别",required=false)
//	@NotNull(message = "必须选择一个仓库类别")
	private Integer type;
	private Integer type_old;
	
	@ApiModelProperty(value="状态",required=false)
	private Integer status;
	private Integer status_old;
	
	@ApiModelProperty(value="面积(区域专用)",required=false)
	@Digits(integer=5, fraction=2, message="面积只能是5位整数和最大2位小数的数字")
	private BigDecimal proportion;
	private BigDecimal proportion_old;
	
	@ApiModelProperty(value="通道号(货架专用)",required=false)
	private String aisle_sn;
	private String aisle_sn_old;
	
	@ApiModelProperty(value="电话(仓库专用)",required=false)
	private String phone;
	private String phone_old;
	
	@ApiModelProperty(value="条码",required=false)
	private String barcode;
	private String barcode_old;
	
	@ApiModelProperty(value="地址(仓库专用)",required=false)
	private String address;
	private String address_old;
	
	@ApiModelProperty(value="描述",required=false)
	private String description;
	private String description_old;
	
	@ApiModelProperty(value="专属部门ID，多个部门ID用英文逗号‘,’分割",required=false)
	private String departments;
	private String departments_old;
	
	@ApiModelProperty(value="员工",required=false)
	private List<WareHouseRelEmp> employees;
	private List<WareHouseRelEmp> employees_old;
	
	
	public boolean hasInfoUpdate() {
		boolean isUpdate = false;
		if(StringUtils.isNull(parent_warehouse_model_inner_sn) && StringUtils.isNotNull(parent_warehouse_model_inner_sn_old)) {
			parent_warehouse_model_inner_sn = -1L;
			isUpdate = true;
		}
		if(StringUtils.isNull(warehouse_type_inner_sn) && StringUtils.isNotNull(warehouse_type_inner_sn_old)) {
			warehouse_type_inner_sn = -1L;
			isUpdate = true;
		}
		if(StringUtils.isNull(administrative_division_sn) && StringUtils.isNotNull(administrative_division_sn_old)) {
			administrative_division_sn = -1L;
			isUpdate = true;
		}
		if((StringUtils.isNotNull(type) || StringUtils.isNotNull(type_old))
				||(StringUtils.isNotNull(parent_warehouse_model_inner_sn) || StringUtils.isNotNull(parent_warehouse_model_inner_sn_old))
				||(StringUtils.isNotNull(warehouse_type_inner_sn) || StringUtils.isNotNull(warehouse_type_inner_sn_old))
				||(StringUtils.isNotNull(administrative_division_sn) || StringUtils.isNotNull(administrative_division_sn_old))
				||(StringUtils.isNotNull(proportion) || StringUtils.isNotNull(proportion_old))
				||(StringUtils.isNotNull(status) || StringUtils.isNotNull(status_old))
				||(StringUtils.isNotEmpty(name) || StringUtils.isNotEmpty(name_old))
				||(StringUtils.isNotEmpty(warehouse_model_sn) || StringUtils.isNotEmpty(warehouse_model_sn_old))
				||(StringUtils.isNotEmpty(aisle_sn) || StringUtils.isNotEmpty(aisle_sn_old))
				||(StringUtils.isNotEmpty(address) || StringUtils.isNotEmpty(address_old))
				||(StringUtils.isNotEmpty(description) || StringUtils.isNotEmpty(description_old))
				||(StringUtils.isNotEmpty(phone) || StringUtils.isNotEmpty(phone_old))
				||(StringUtils.isNotEmpty(barcode) || StringUtils.isNotEmpty(barcode_old))
				||(StringUtils.isNotNull(parent_warehouse_model_inner_sn) || StringUtils.isNotNull(parent_warehouse_model_inner_sn_old))) {
			isUpdate = true;
		}
		return isUpdate;
	}
	
	public boolean hasDeptUpdate() {
		return (StringUtils.isNotEmpty(departments) || StringUtils.isNotEmpty(departments_old));
	}
	
	public boolean hasEmpUpdate() {
		return (StringUtils.isNotEmpty(employees) || StringUtils.isNotEmpty(employees_old));
	}
}
