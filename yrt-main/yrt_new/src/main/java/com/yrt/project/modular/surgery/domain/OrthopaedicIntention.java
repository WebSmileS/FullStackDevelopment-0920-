package com.yrt.project.modular.surgery.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrthopaedicIntention extends OrthopaedicIntentionKey {

	@ApiModelProperty(value = "医院名称")
	private String hospital_name;

	@ApiModelProperty(value = "医院别名")
	private String hospital_alias;

	@ApiModelProperty(value = "经销商名称")
	private String dealer_name;
	@ApiModelProperty(value = "开始日期")

	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date begin_date;

	@ApiModelProperty(value = "截止日期")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date end_date;

	@ApiModelProperty(value = "描述")
	private String description;

	@ApiModelProperty(value = "状态 ")
	private Integer status;

	// None master table fields

	// ref operation-types
	@ApiModelProperty(value = "颅骨状态")
	private Integer t0_status;
	@ApiModelProperty(value = "颅骨状态名称")
	private String t0_name;
	@ApiModelProperty(value = "脊柱状态")
	private Integer t1_status;
	@ApiModelProperty(value = "脊柱状态名称")
	private String t1_name;
	@ApiModelProperty(value = "关节状态")
	private Integer t2_status;
	@ApiModelProperty(value = "关节状态名称")
	private String t2_name;
	@ApiModelProperty(value = "创伤状态")
	private Integer t3_status;
	@ApiModelProperty(value = "创伤状态名称")
	private String t3_name;

	// ref brands
	@ApiModelProperty(value = "关联品牌json", hidden = true)
	@JsonIgnore
	transient private String rel_brands_json;

	@ApiModelProperty(value = "关联品牌(brand_inner_sn, name, status), status取值同 t0_status...")
	private List<IntentionRelBrand> rel_brands = new ArrayList<>();

}
